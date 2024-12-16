package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.UserTrackLikeRepository;
import com.munecting.api.domain.like.dto.response.*;
import com.munecting.api.domain.like.dao.TrackLikeRepository;
import com.munecting.api.domain.like.entity.TrackLike;
import com.munecting.api.domain.like.entity.UserTrackLike;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.user.service.UserService;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final TrackLikeRepository trackLikeRepository;
    private final UserTrackLikeRepository userTrackLikeRepository;
    private final SpotifyService spotifyService;
    private final UserService userService;

    // todo: 삭제
    @Transactional(readOnly = true)
    public boolean isTrackLikedByUser(String trackId, Long userId) {
        return userTrackLikeRepository.existsByUserIdAndTrackId(userId, trackId);
    }

    @Transactional(readOnly = true)
    public GetLikePlaylistResponseDto getLikedTracks(Long userId, Long cursor, int size) {
        userService.validateUserExists(userId);
        
        Slice<UserTrackLike> likes = getLikeRecords(userId, cursor, size);

        List<String> trackIds = extractTrackIdsFrom(likes);
        Map<String, TrackResponseDto> trackInfoByTrackId = getTrackInfos(trackIds);

        List<LikeTrackResponseDto> likedTracks = mapToLikeTrackResponseDto(likes, trackInfoByTrackId);

        return GetLikePlaylistResponseDto.of(likes.isEmpty(), likes.hasNext(), likedTracks);
    }

    private Slice<UserTrackLike> getLikeRecords(Long userId, Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        return getLikeSlice(userId, cursor, pageable);
    }

    private Slice<UserTrackLike> getLikeSlice(Long userId, Long cursor, Pageable pageable) {
        if (cursor == null) {
            return userTrackLikeRepository.findByUserId(userId, pageable);
        } else {
            return userTrackLikeRepository.findByUserId(userId, cursor, pageable);
        }
    }

    private List<String> extractTrackIdsFrom(Slice<UserTrackLike> likes) {
        return likes.stream()
                .map(UserTrackLike::getTrackId)
                .collect(Collectors.toList());
    }

    private Map<String, TrackResponseDto> getTrackInfos(List<String> trackIds) {
        return spotifyService.getLikeTrackInfoMap(trackIds);
    }

    private List<LikeTrackResponseDto> mapToLikeTrackResponseDto(Slice<UserTrackLike> likes, Map<String, TrackResponseDto> trackInfoByTrackId) {
        return likes.stream()
                .map(like -> LikeTrackResponseDto.of(
                        like.getId(),
                        trackInfoByTrackId.get(like.getTrackId())))
                .collect(Collectors.toList());
    }

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(100))
    @Transactional
    public LikeResponseDto toggleTrackLike(String trackId, Long userId) {
        spotifyService.validateTrackExists(trackId);
        userService.validateUserExists(userId);

        UserTrackLike userLike = userTrackLikeRepository
                .findByTrackIdAndUserId(trackId, userId)
                .orElseGet(() -> {
                    UserTrackLike newUserLike = UserTrackLike.toEntity(userId, trackId, false);
                    return userTrackLikeRepository.save(newUserLike);
                });

        userLike.toggle();

        TrackLike trackLike = trackLikeRepository.findByTrackId(trackId)
                .orElseGet(() -> trackLikeRepository.save(TrackLike.toEntity(0, trackId)));

        if (userLike.isLiked()) {
            trackLike.increaseLikeCount();
        }

        if (!userLike.isLiked()) {
            trackLike.decreaseLikeCount();
        }

        return LikeResponseDto.of(userLike.isLiked(), trackLike.getLikeCount());
    }

    @Recover
    public LikeResponseDto recoverToggleTrackLike(String trackId, Long userId) {
        log.warn("트랙 아이디- {}에 대한 userId- {}의 좋아요 요청 처리 중 문제가 발생하였습니다. ", trackId, userId);
        throw new ConflictException(Status.LIKE_REQUEST_CONFLICT);
    }

}

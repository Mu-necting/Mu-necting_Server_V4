package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dto.response.*;
import com.munecting.api.global.aop.annotation.DistributedLock;
import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.like.entity.Like;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final SpotifyService spotifyService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public boolean isTrackLikedByUser(String trackId, Long userId) {
        return likeRepository.existsByUserIdAndTrackId(userId, trackId);
    }

    @DistributedLock(key = "#trackId + ':' + #userId" )
    public AddTrackLikeResponseDto addTrackLike(String trackId, Long userId) {
        spotifyService.validateTrackExists(trackId);
        userService.validateUserExists(userId);

        boolean isLikedTrack = likeRepository.existsByUserIdAndTrackId(userId, trackId);
        if (!isLikedTrack) {
            Like like = Like.toEntity(userId, trackId);
            likeRepository.save(like);
            isLikedTrack = true;
        }

        int likeCount = likeRepository.countByTrackId(trackId);
        return AddTrackLikeResponseDto.of(trackId,likeCount, isLikedTrack);
    }

    @Transactional(readOnly = true)
    public GetLikePlaylistResponseDto getLikedTracks(Long userId, Long cursor, int size) {
        userService.validateUserExists(userId);
        
        Slice<Like> likes = getLikeRecords(userId, cursor, size);

        List<String> trackIds = extractTrackIdsFrom(likes);
        Map<String, TrackResponseDto> trackInfoByTrackId = getTrackInfos(trackIds);

        List<LikeTrackResponseDto> likedTracks = mapToLikeTrackResponseDto(likes, trackInfoByTrackId);

        return GetLikePlaylistResponseDto.of(likes.isEmpty(), likes.hasNext(), likedTracks);
    }

    private Slice<Like> getLikeRecords(Long userId, Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        return getLikeSlice(userId, cursor, pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Like> getLikeSlice(Long userId, Long cursor, Pageable pageable) {
        if (cursor == null) {
            return likeRepository.findByUserId(userId, pageable);
        } else {
            return likeRepository.findByUserId(userId, cursor, pageable);
        }
    }

    private List<String> extractTrackIdsFrom(Slice<Like> likes) {
        return likes.stream()
                .map(Like::getTrackId)
                .collect(Collectors.toList());
    }

    private Map<String, TrackResponseDto> getTrackInfos(List<String> trackIds) {
        return spotifyService.getLikeTrackInfoMap(trackIds);
    }

    private List<LikeTrackResponseDto> mapToLikeTrackResponseDto(Slice<Like> likes, Map<String, TrackResponseDto> trackInfoByTrackId) {
        return likes.stream()
                .map(like -> LikeTrackResponseDto.of(
                        like.getId(),
                        trackInfoByTrackId.get(like.getTrackId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public DeleteTrackLikeResponseDto deleteTrackLike(String trackId, Long userId) {
        spotifyService.validateTrackExists(trackId);
        userService.validateUserExists(userId);

        boolean isLikedTrack = isTrackLikedByUser(trackId, userId);
        if (isLikedTrack) {
            likeRepository.deleteByTrackIdAndUserId(trackId, userId);
            isLikedTrack = false;
        }

        int likeCount = likeRepository.countByTrackId(trackId);
        return DeleteTrackLikeResponseDto.of(trackId, isLikedTrack, likeCount);
    }
}

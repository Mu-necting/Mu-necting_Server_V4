package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.like.dto.response.AddTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.DeleteTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.GetLikedTrackListResponseDto;
import com.munecting.api.domain.like.dto.response.GetLikedTrackResponseDto;
import com.munecting.api.domain.like.entity.Like;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final SpotifyService spotifyService;
    private final UserService userService;

    public boolean isTrackLikedByUser(String trackId, Long userId) {
        return likeRepository.existsByUserIdAndTrackId(userId, trackId);
    }

    @Transactional
    public AddTrackLikeResponseDto addTrackLike(String trackId, Long userId) {
        spotifyService.validateTrackId(trackId);
        userService.validateUserExists(userId);

        boolean isLikedTrack = isTrackLikedByUser(trackId, userId);
        if (!isLikedTrack) {
            Like like = Like.toEntity(userId, trackId);
            likeRepository.save(like);
            isLikedTrack = true;
        }

        int likeCount = likeRepository.countByTrackId(trackId);
        return AddTrackLikeResponseDto.of(trackId,likeCount, isLikedTrack);
    }

    @Transactional(readOnly = true)
    public GetLikedTrackListResponseDto getLikedTracks(Long userId, Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Like> likes = getLikeSlice(userId, cursor, pageable);

        List<GetLikedTrackResponseDto> likedTracks = likes.stream()
                .map(like -> {
                    String trackId = like.getTrackId();
                    return spotifyService.getLikedTrack(trackId, like.getId());
                })
                .collect(Collectors.toList());

        return GetLikedTrackListResponseDto.of(likes.isEmpty(), likes.hasNext(), likedTracks);
    }

    private Slice<Like> getLikeSlice(Long userId, Long cursor, Pageable pageable) {
        if (cursor == null) {
            return likeRepository.findByUserId(userId, pageable);
        } else {
            return likeRepository.findByUserId(userId, cursor, pageable);
        }
    }

    @Transactional
    public DeleteTrackLikeResponseDto deleteTrackLike(String trackId, Long userId) {
        spotifyService.validateTrackId(trackId);
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

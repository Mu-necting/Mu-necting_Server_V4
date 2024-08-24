package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.like.dto.response.AddTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.DeleteTrackLikeResponseDto;
import com.munecting.api.domain.like.entity.Like;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

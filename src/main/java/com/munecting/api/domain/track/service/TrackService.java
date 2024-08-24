package com.munecting.api.domain.track.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.track.dto.response.GetTrackDetailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final SpotifyService spotifyService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public GetTrackDetailsResponseDto getTrackDetails(String trackId) {
        spotifyService.validateTrackId(trackId);

        int likeCount = likeRepository.countByTrackId(trackId);
        int commentCount = commentRepository.countByTrackId(trackId);

        return GetTrackDetailsResponseDto.of(likeCount, commentCount);
    }
}


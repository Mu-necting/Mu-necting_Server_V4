package com.munecting.api.domain.like.dto.response;

import com.wrapper.spotify.model_objects.specification.Image;
import lombok.Builder;

import java.util.List;

@Builder
public record GetLikedTrackResponseDto(
        Long likeId,
        String trackId,
        String trackTitle,
        List<LikedTrackArtistResponseDto> artists,
        String trackPreview,
        Image[] images
) {
}

package com.munecting.api.domain.like.dto.response;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import lombok.Builder;

@Builder
public record LikeArtistResponseDto(
        String artistName
) {

    public static LikeArtistResponseDto of(ArtistSimplified artistSimplified) {
        return LikeArtistResponseDto.builder()
                .artistName(artistSimplified.getName())
                .build();
    }
}

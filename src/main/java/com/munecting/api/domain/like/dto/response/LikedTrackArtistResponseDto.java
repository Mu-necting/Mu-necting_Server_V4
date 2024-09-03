package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record LikedTrackArtistResponseDto(
        String artistName
) {

    public static LikedTrackArtistResponseDto of(String artistName) {
        return LikedTrackArtistResponseDto.builder()
                .artistName(artistName)
                .build();
    }
}

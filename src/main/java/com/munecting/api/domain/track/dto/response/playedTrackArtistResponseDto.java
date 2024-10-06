package com.munecting.api.domain.track.dto.response;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import lombok.Builder;

@Builder
public record playedTrackArtistResponseDto(
        String artistName
) {

    public static playedTrackArtistResponseDto of(ArtistSimplified artistSimplified) {
        return playedTrackArtistResponseDto.builder()
                .artistName(artistSimplified.getName())
                .build();
    }
}

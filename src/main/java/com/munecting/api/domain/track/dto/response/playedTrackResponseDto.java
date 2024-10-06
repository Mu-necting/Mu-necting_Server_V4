package com.munecting.api.domain.track.dto.response;

import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import lombok.Builder;

import java.util.List;

@Builder
public record playedTrackResponseDto(
        String trackId,
        String trackTitle,
        List<playedTrackArtistResponseDto> artists,
        String trackPreview,
        Image[] images
) {

    public static playedTrackResponseDto of(Track track, List<playedTrackArtistResponseDto> playedTrackArtistResponseDtos) {
        return playedTrackResponseDto.builder()
                .trackId(track.getId())
                .trackTitle(track.getName())
                .artists(playedTrackArtistResponseDtos)
                .trackPreview(track.getPreviewUrl())
                .images(track.getAlbum().getImages())
                .build();
    }
}

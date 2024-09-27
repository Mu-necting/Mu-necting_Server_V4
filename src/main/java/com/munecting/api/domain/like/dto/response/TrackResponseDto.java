package com.munecting.api.domain.like.dto.response;

import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import lombok.Builder;

import java.util.List;

@Builder
public record TrackResponseDto(
        String trackId,
        String trackTitle,
        List<LikeArtistResponseDto> artists,
        String trackPreview,
        Image[] images
) {

    public static TrackResponseDto of(Track track, List<LikeArtistResponseDto> likeArtistResponseDtos) {
        return TrackResponseDto.builder()
                .trackId(track.getId())
                .trackTitle(track.getName())
                .artists(likeArtistResponseDtos)
                .trackPreview(track.getPreviewUrl())
                .images(track.getAlbum().getImages())
                .build();
    }
}

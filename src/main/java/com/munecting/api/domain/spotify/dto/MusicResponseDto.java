package com.munecting.api.domain.spotify.dto;

import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record MusicResponseDto (
         String trackId,
         String trackUri,
         String trackTitle,
         String trackPreview,
         List<ArtistResponseDto> artists,
         Image[] images
) {
    public static MusicResponseDto of(TrackSimplified trackSimplified, List<ArtistResponseDto> artistResponseDtos) {
        return MusicResponseDto.builder()
                .trackUri(trackSimplified.getUri())
                .trackId(trackSimplified.getId())
                .trackTitle(trackSimplified.getName())
                .trackPreview(trackSimplified.getPreviewUrl())
                .artists(artistResponseDtos)
                .build();
    }

    public static MusicResponseDto of(Track track, List<ArtistResponseDto> artistResponseDtos) {
        return MusicResponseDto.builder()
                .trackUri(track.getUri())
                .trackId(track.getId())
                .trackTitle(track.getName())
                .trackPreview(track.getPreviewUrl())
                .artists(artistResponseDtos)
                .build();
    }
}
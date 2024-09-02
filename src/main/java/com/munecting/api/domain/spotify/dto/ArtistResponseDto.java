package com.munecting.api.domain.spotify.dto;

import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record ArtistResponseDto(
        String artistName,
        String artistId,
        String artistUri,
        Image[] images
) {
    public static ArtistResponseDto of(ArtistSimplified artistSimplified) {
        return ArtistResponseDto.builder()
                .artistId(artistSimplified.getId())
                .artistUri(artistSimplified.getUri())
                .artistId(artistSimplified.getId())
                .artistName(artistSimplified.getName())
                .build();
    }

    public static ArtistResponseDto of(Artist artist) {
        return ArtistResponseDto.builder()
                .artistId(artist.getId())
                .artistName(artist.getName())
                .artistUri(artist.getUri())
                .images(artist.getImages())
                .build();
    }
}
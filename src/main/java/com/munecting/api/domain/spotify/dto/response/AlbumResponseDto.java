package com.munecting.api.domain.spotify.dto.response;

import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import java.util.List;
import lombok.Builder;

@Builder
public record AlbumResponseDto (
        String albumId,
        String albumUri,
        String albumTitle,
        List<ArtistResponseDto> artists,
        Image[] images
) {

    public static AlbumResponseDto of (AlbumSimplified albumSimplified, List<ArtistResponseDto> artists) {
        return  AlbumResponseDto.builder()
                .albumId(albumSimplified.getId())
                .albumUri(albumSimplified.getUri())
                .albumTitle(albumSimplified.getName())
                .artists(artists)
                .images(albumSimplified.getImages())
                .build();
    }
}





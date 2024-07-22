package com.munecting.api.domain.spotify.dto;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AlbumResponseDto {

    private String albumId;
    private String albumUri;
    private String albumTitle;
    private List<ArtistResponseDto> artists;
    private Image[] images;

}

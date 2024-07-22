package com.munecting.api.domain.spotify.dto;

import com.wrapper.spotify.model_objects.specification.Image;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ArtistResponseDto {

    private String artistName;
    private String artistId;
    private String artistUri;
    private Image[] images;
}
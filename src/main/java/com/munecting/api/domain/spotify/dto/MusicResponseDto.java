package com.munecting.api.domain.spotify.dto;

import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MusicResponseDto {

    private String trackId;
    private String trackUri;
    private String trackTitle;
    private String trackPreview;
    private List<ArtistResponseDto> artists;
    private Image[] images;
}

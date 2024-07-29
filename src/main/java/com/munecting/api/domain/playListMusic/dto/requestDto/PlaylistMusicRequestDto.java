package com.munecting.api.domain.playListMusic.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PlaylistMusicRequestDto {

    private Long playlistId;

    private String trackId;

}

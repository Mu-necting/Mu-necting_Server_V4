package com.munecting.api.domain.playListMusic.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PlaylistMusicPlayOrderRequestDto {

    private String trackId;

    private Long playOrder;

}

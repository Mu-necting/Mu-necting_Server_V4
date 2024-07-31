package com.munecting.api.domain.playList.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PlaylistResponseDto {

    private Long id;

    private String name;

}

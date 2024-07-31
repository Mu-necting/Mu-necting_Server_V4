package com.munecting.api.domain.playListMusic.dto.requestDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistMusicPlayOrderUpdateRequestDto {

    private List<PlaylistMusicPlayOrderRequestDto> playlistMusicPlayOrderRequestDtoList;

}

package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetLikePlaylistResponseDto(
        Boolean isEmpty,
        Boolean hasNext,
        List<LikeTrackResponseDto> likePlaylist
){
    
    public static GetLikePlaylistResponseDto of(
            Boolean isEmpty,
            Boolean hasNext,
            List<LikeTrackResponseDto> tracks
    ) {
        return GetLikePlaylistResponseDto.builder()
                .isEmpty(isEmpty)
                .hasNext(hasNext)
                .likePlaylist(tracks)
                .build();
    }
}

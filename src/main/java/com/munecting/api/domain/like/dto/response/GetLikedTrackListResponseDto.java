package com.munecting.api.domain.like.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record GetLikedTrackListResponseDto(
        Boolean isEmpty,
        Boolean hasNext,
        List<GetLikedTrackResponseDto> likedTracks
){
    
    public static GetLikedTrackListResponseDto of(
            Boolean isEmpty,
            Boolean hasNext,
            List<GetLikedTrackResponseDto> tracks
    ) {
        return GetLikedTrackListResponseDto.builder()
                .isEmpty(isEmpty)
                .hasNext(hasNext)
                .likedTracks(tracks)
                .build();
    }
}

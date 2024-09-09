package com.munecting.api.domain.track.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetRecentPlaylistResponseDto(
        Boolean isEmpty,
        Boolean hasNext,
        List<RecentlyPlayedTrackResponseDto> tracks
) {
    public static GetRecentPlaylistResponseDto of(
            Boolean isEmpty,
            Boolean hasNext,
            List<RecentlyPlayedTrackResponseDto> recentlyPlayedTrackInfos
    ) {
        return GetRecentPlaylistResponseDto.builder()
                .isEmpty(isEmpty)
                .hasNext(hasNext)
                .tracks(recentlyPlayedTrackInfos)
                .build();
    }
}

package com.munecting.api.domain.track.dto.response;

import lombok.Builder;

@Builder
public record RecentlyPlayedTrackResponseDto(
        Long recentlyPlayedId,
        playedTrackResponseDto track
) {

    public static RecentlyPlayedTrackResponseDto of(Long id, playedTrackResponseDto playedTrackResponseDto) {
        return RecentlyPlayedTrackResponseDto.builder()
                .recentlyPlayedId(id)
                .track(playedTrackResponseDto)
                .build();
    }
}

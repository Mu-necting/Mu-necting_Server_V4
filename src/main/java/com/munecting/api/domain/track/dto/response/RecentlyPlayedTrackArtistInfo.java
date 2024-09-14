package com.munecting.api.domain.track.dto.response;

import lombok.Builder;

@Builder
public record RecentlyPlayedTrackArtistInfo(
        String artistName
) {
    public static RecentlyPlayedTrackArtistInfo of(String artistName) {
        return RecentlyPlayedTrackArtistInfo.builder()
                .artistName(artistName)
                .build();
    }
}

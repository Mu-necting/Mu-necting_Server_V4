package com.munecting.api.domain.track.dto.response;

import com.wrapper.spotify.model_objects.specification.Image;
import lombok.Builder;

import java.util.List;

@Builder
public record RecentlyPlayedTrackResponseDto(
        Long recentlyPlayedId,
        String trackId,
        String trackTitle,
        List<RecentlyPlayedTrackArtistInfo> artists,
        String trackPreview,
        Image[] images
) {}

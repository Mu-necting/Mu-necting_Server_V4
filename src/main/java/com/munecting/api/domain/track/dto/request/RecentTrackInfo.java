package com.munecting.api.domain.track.dto.request;

import jakarta.validation.constraints.NotNull;

public record RecentTrackInfo(

        @NotNull
        String trackId,

        @NotNull
        Integer order

) {}

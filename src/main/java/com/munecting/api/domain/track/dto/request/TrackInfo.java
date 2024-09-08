package com.munecting.api.domain.track.dto.request;

import jakarta.validation.constraints.NotNull;

public record TrackInfo(

        @NotNull
        String trackId,

        @NotNull
        Integer order

) {}

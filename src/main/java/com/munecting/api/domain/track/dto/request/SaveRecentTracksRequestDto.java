package com.munecting.api.domain.track.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SaveRecentTracksRequestDto(

        @NotNull
        List<TrackInfo> tracks

) {}

package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record LikeTrackResponseDto(
        Long likeId,
        TrackResponseDto track
) {

    public static LikeTrackResponseDto of(Long likeId, TrackResponseDto trackResponseDto) {
        return LikeTrackResponseDto.builder()
                .likeId(likeId)
                .track(trackResponseDto)
                .build();
    }
}

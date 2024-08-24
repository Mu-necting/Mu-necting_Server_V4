package com.munecting.api.domain.track.dto.response;

import lombok.Builder;

@Builder
public record GetTrackDetailsResponseDto(
        int likeCount,
        int commentCount
) {
    public static GetTrackDetailsResponseDto of (int likeCount, int commentCount) {
        return GetTrackDetailsResponseDto.builder()
                .likeCount(likeCount)
                .commentCount(commentCount)
                .build();
    }
}

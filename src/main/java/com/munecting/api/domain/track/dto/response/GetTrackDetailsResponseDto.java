package com.munecting.api.domain.track.dto.response;

import lombok.Builder;

@Builder
public record GetTrackDetailsResponseDto(
        boolean isLiked,
        int likeCount,
        int commentCount
) {
    public static GetTrackDetailsResponseDto of (boolean isLiked, int likeCount, int commentCount) {
        return GetTrackDetailsResponseDto.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .build();
    }
}

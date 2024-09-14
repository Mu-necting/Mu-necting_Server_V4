package com.munecting.api.domain.track.dto.response;

import lombok.Builder;

@Builder
public record GetTrackDetailsResponseDto(
        Boolean isLiked,
        Integer likeCount,
        Integer commentCount
) {

    public static GetTrackDetailsResponseDto of (Boolean isLiked, Integer likeCount, Integer commentCount) {
        return GetTrackDetailsResponseDto.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .build();
    }
}

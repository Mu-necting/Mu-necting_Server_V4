package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record LikeResponseDto(
        Boolean userLiked,
        Integer likeCount
) {

    public static LikeResponseDto of(Boolean userLiked, Integer likeCount) {
        return LikeResponseDto.builder()
                .userLiked(userLiked)
                .likeCount(likeCount)
                .build();
    }

}

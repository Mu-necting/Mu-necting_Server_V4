package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record AddTrackLikeResponseDto(
        String trackId,
        Boolean isLiked,
        Integer likeCount
) {

    public static AddTrackLikeResponseDto of(String trackId, Integer likeCount, Boolean isLiked) {
        return AddTrackLikeResponseDto.builder()
                .trackId(trackId)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}

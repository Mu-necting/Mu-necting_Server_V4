package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record AddTrackLikeResponseDto(
        String trackId,
        boolean isLiked,
        int likeCount
) {
    public static AddTrackLikeResponseDto of(String trackId, int likeCount, boolean isLiked) {
        return AddTrackLikeResponseDto.builder()
                .trackId(trackId)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}

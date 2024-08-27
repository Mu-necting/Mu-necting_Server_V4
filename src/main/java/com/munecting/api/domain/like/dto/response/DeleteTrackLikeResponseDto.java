package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record DeleteTrackLikeResponseDto(
        String trackId,
        boolean isLiked,
        int likeCount
) {
    public static DeleteTrackLikeResponseDto of(String trackId, boolean isLiked, int likeCount) {
        return DeleteTrackLikeResponseDto.builder()
                .trackId(trackId)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}

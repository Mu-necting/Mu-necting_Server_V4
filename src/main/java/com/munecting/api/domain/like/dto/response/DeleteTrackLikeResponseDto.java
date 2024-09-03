package com.munecting.api.domain.like.dto.response;

import lombok.Builder;

@Builder
public record DeleteTrackLikeResponseDto(
        String trackId,
        Boolean isLiked,
        Integer likeCount
) {

    public static DeleteTrackLikeResponseDto of(String trackId, Boolean isLiked, Integer likeCount) {
        return DeleteTrackLikeResponseDto.builder()
                .trackId(trackId)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}

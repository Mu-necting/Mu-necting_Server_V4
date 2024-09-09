package com.munecting.api.domain.comment.dto.response;

import lombok.Builder;

@Builder
public record CommentIdResponseDto (
        Long commentId
) {

    public static CommentIdResponseDto of(Long commentId) {
        return new CommentIdResponseDtoBuilder()
                .commentId(commentId)
                .build();
    }
}

package com.munecting.api.domain.comment.dto.request;

public record CommentRequestDto(
        Long userId,
        String trackId,
        String content
) {}
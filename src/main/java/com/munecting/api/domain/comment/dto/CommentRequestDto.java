package com.munecting.api.domain.comment.dto;

public record CommentRequestDto(
        Long userId,
        String trackId,
        String content
) {}
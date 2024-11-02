package com.munecting.api.domain.comment.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentRequestDto(

        @NotNull(message = "trackId는 필수값입니다.")
        String trackId,

        @NotNull(message = "댓글 내용은 필수값입니다.")
        String content

) {}
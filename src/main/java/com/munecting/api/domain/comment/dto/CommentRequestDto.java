package com.munecting.api.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CommentRequestDto {

    private Long userId;

    private String trackId;

    private String content;

}

package com.munecting.api.domain.comment.dto.response;

import com.munecting.api.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentResponseDto (
         Long userId,
         String trackId,
         String content,
         LocalDateTime createdAt,
         LocalDateTime updatedAt
         // 내가 쓴 댓글인지 판별하는 필드 필요 -> 인증 부분 완성되면 추가 예정
) {

    public static CommentResponseDto of(Comment comment) {
        return CommentResponseDto.builder()
                .userId(comment.getUserId())
                .trackId(comment.getTrackId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

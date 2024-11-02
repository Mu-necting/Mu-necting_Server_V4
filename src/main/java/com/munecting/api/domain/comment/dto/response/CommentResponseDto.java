package com.munecting.api.domain.comment.dto.response;

import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.user.dto.response.UserResponseDto;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentResponseDto (
         UserResponseDto userResponseDto,
         String trackId,
         String content,
         LocalDateTime createdAt,
         LocalDateTime updatedAt,
         Boolean isOwner
) {

    public static CommentResponseDto of(UserResponseDto userResponseDto, Comment comment, Boolean isOwner) {
        return CommentResponseDto.builder()
                .userResponseDto(userResponseDto)
                .trackId(comment.getTrackId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isOwner(isOwner)
                .build();
    }
}

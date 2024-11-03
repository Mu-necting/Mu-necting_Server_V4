package com.munecting.api.domain.comment.dto.response;

import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.user.dto.response.UserResponseDto;
import com.munecting.api.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentResponseDto (
         UserResponseDto userDto,
         String trackId,
         String content,
         LocalDateTime createdAt,
         LocalDateTime updatedAt,
         Boolean isOwner
) {

    public static CommentResponseDto of(User user, Comment comment, Boolean isOwner) {
        UserResponseDto userDto = UserResponseDto.of(
                user.getId(), user.getNickname(), user.getProfileImageUrl());

        return CommentResponseDto.builder()
                .userDto(userDto)
                .trackId(comment.getTrackId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isOwner(isOwner)
                .build();
    }
}

package com.munecting.api.domain.comment.dto;

import com.munecting.api.domain.comment.entity.Comment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

@Builder
@Getter
@Setter
public class CommentResponseDto {

    private Long userId;

    private String trackId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    // 내가 쓴 댓글인지 판별하는 필드 필요 -> 인증 부분 완성되면 추가 예정

    public static CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .userId(comment.getUserId())
                .trackId(comment.getTrackId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

}

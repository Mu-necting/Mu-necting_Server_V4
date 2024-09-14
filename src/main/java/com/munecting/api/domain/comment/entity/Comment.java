package com.munecting.api.domain.comment.entity;

import com.munecting.api.domain.comment.dto.request.CommentRequestDto;
import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String trackId;

    @NotNull
    private String content;

    public void updateContent(String content) {
        this.content = content;
    }

    public static Comment toEntity(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .userId(commentRequestDto.userId())
                .trackId(commentRequestDto.trackId())
                .content(commentRequestDto.content())
                .build();
    }
}

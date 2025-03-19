package com.munecting.api.domain.like.entity;

import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "trackId"}))
public class UserTrackLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String trackId;

    private boolean isLiked;

    @Version
    private Integer version;

    public static UserTrackLike toEntity(Long userId, String trackId, boolean isLiked) {
        return UserTrackLike.builder()
                .trackId(trackId)
                .userId(userId)
                .isLiked(isLiked)
                .build();
    }

    public void toggle() {
        isLiked = !isLiked;
    }

}

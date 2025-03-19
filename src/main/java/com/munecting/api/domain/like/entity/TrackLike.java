package com.munecting.api.domain.like.entity;

import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.*;
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
public class TrackLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String trackId;

    @NotNull
    private Integer likeCount;

    @Version
    private Integer version;

    public static TrackLike toEntity(Integer likeCount, String trackId) {
        return TrackLike.builder()
                .trackId(trackId)
                .likeCount(likeCount)
                .build();
    }

    public void increaseLikeCount() {
        ++likeCount;
    }

    public void decreaseLikeCount() {
        --likeCount;
    }

}

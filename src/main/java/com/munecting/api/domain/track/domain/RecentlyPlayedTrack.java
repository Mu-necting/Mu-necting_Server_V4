package com.munecting.api.domain.track.domain;

import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class RecentlyPlayedTrack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private String trackId;

    @NotNull
    private Long userId;

    public static RecentlyPlayedTrack toEntity(String trackId, Long userId) {
        return RecentlyPlayedTrack.builder()
                .trackId(trackId)
                .userId(userId)
                .build();
    }

    public void setCreatedAt(LocalDateTime localDateTime) {
        this.overrideCreatedAt(localDateTime);
    }
}

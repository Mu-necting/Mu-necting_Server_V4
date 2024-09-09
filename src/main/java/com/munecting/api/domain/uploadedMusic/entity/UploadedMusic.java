package com.munecting.api.domain.uploadedMusic.entity;

import com.munecting.api.domain.uploadedMusic.dto.request.MusicRequestDto;
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
public class UploadedMusic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String trackId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Integer uploadDuration;

    public static UploadedMusic toEntity (MusicRequestDto musicRequestDto) {
        return UploadedMusic.builder().
                userId(musicRequestDto.userId())
                .trackId(musicRequestDto.trackId())
                .latitude(musicRequestDto.latitude())
                .longitude(musicRequestDto.longitude())
                .uploadDuration(musicRequestDto.uploadDuration())
                .build();
    }
}

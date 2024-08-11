package com.munecting.api.domain.uploadedMusic.dto;

import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UploadedMusicResponseDto {

    private Long id;

    private MusicResponseDto musicResponseDto;

    private User user;

    private Double latitude;

    private Double longitude;

    public static UploadedMusicResponseDto toDto(UploadedMusic uploadedMusic, MusicResponseDto musicResponseDto, User user) {
        return UploadedMusicResponseDto.builder()
                .id(uploadedMusic.getId())
                .musicResponseDto(musicResponseDto)
                .user(user)
                .latitude(uploadedMusic.getLatitude())
                .longitude(uploadedMusic.getLongitude())
                .build();
    }

}

package com.munecting.api.domain.uploadedMusic.dto.response;

import com.munecting.api.domain.spotify.dto.response.MusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.user.dto.response.UserResponseDto;
import com.munecting.api.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UploadedMusicResponseDto(
        Long id,
        MusicResponseDto musicResponseDto,
        Double latitude,
        Double longitude,
        UserResponseDto userResponseDto
) {

    public static UploadedMusicResponseDto of(UploadedMusic uploadedMusic, MusicResponseDto musicResponseDto, User user) {
        UserResponseDto userDto = UserResponseDto.of(
                user.getId(), user.getNickname(), user.getProfileImageUrl());

        return UploadedMusicResponseDto.builder()
                .id(uploadedMusic.getId())
                .musicResponseDto(musicResponseDto)
                .latitude(uploadedMusic.getLatitude())
                .longitude(uploadedMusic.getLongitude())
                .userResponseDto(userDto)
                .build();
    }
}





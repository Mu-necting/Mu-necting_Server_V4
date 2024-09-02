package com.munecting.api.domain.uploadedMusic.dto;

import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record UploadedMusicResponseDto(
        Long id,
        MusicResponseDto musicResponseDto,
        Double latitude,
        Double longitude
        //인증 작업 완료 후 User 정보 dto로 반환하기
) {
    public static UploadedMusicResponseDto of(UploadedMusic uploadedMusic, MusicResponseDto musicResponseDto) {
        return UploadedMusicResponseDto.builder()
                .id(uploadedMusic.getId())
                .musicResponseDto(musicResponseDto)
                .latitude(uploadedMusic.getLatitude())
                .longitude(uploadedMusic.getLongitude())
                .build();
    }
}





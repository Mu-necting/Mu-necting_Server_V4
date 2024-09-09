package com.munecting.api.domain.uploadedMusic.dto.response;

import com.munecting.api.domain.spotify.dto.response.MusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import lombok.Builder;

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





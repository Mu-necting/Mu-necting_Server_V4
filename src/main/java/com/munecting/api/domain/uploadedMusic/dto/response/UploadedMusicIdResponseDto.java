package com.munecting.api.domain.uploadedMusic.dto.response;

import lombok.Builder;

@Builder
public record UploadedMusicIdResponseDto (
        Long uploadedMusicId
) {

    public static UploadedMusicIdResponseDto of(Long uploadedMusicId) {
        return UploadedMusicIdResponseDto.builder()
                .uploadedMusicId(uploadedMusicId)
                .build();
    }
}

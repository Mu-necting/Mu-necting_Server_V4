package com.munecting.api.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UpdateProfileResponseDto(
        String nickname,
        String imageUrl
) {

    public static UpdateProfileResponseDto of(String nickname, String imgUrl) {
        return UpdateProfileResponseDto.builder()
                .nickname(nickname)
                .imageUrl(imgUrl)
                .build();
    }
}

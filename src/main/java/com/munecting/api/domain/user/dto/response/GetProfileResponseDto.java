package com.munecting.api.domain.user.dto.response;

import lombok.Builder;

@Builder
public record GetProfileResponseDto(
        String profileImage,
        String nickname
) {

    public static GetProfileResponseDto of(String profileImageUrl, String nickname) {
        return GetProfileResponseDto.builder()
                .profileImage(profileImageUrl)
                .nickname(nickname)
                .build();
    }
}

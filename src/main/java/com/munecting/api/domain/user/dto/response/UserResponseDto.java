package com.munecting.api.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String nickname,
        String imageUrl
) {
    public static UserResponseDto of(Long id, String nickname, String imgUrl) {
        return UserResponseDto.builder()
                .id(id)
                .nickname(nickname)
                .imageUrl(imgUrl)
                .build();
    }
}
package com.munecting.api.domain.user.dto.response;

import lombok.Builder;

@Builder
public record ValidateTokenResponseDto(
        boolean isValid
) {

    public static ValidateTokenResponseDto of(boolean isValid) {
        return ValidateTokenResponseDto.builder()
                .isValid(isValid)
                .build();
    }

}

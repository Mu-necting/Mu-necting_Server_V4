package com.munecting.api.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(

        @NotBlank
        String refreshToken

) {}

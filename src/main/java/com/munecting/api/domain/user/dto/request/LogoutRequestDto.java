package com.munecting.api.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto(

        @NotBlank
        String accessToken

) {}

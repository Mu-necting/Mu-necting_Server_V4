package com.munecting.api.domain.user.dto.request;

import com.munecting.api.domain.user.constant.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(

        @NotNull(message = "socialType은 필수값입니다.")
        @Schema(description = "Identity Provider")
        SocialType socialType,

        @NotBlank
        String idToken
) {}

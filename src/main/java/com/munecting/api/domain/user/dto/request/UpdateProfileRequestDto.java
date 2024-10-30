package com.munecting.api.domain.user.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileRequestDto(
        String nickname,
        MultipartFile profileImage
) {
}

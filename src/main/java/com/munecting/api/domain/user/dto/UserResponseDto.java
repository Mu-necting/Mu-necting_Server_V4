package com.munecting.api.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Builder
public class UserResponseDto {
    private String email;
    private String nickname;
}

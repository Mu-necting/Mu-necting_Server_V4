package com.munecting.api.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserRegisterDto {
    private String email;
    private String nickname;
    private String password;
}

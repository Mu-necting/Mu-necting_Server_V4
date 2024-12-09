package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.dto.response.ValidateTokenResponseDto;
import com.munecting.api.global.auth.jwt.JwtProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("주어진 액세스 토큰이 유효하면 true를 반환한다.")
    @Test
    public void validateAccessToken(){
        //given
        String accessToken = "Bearer " + jwtProvider.getIssueToken(1L, true);

        //when
        ValidateTokenResponseDto response = authService.validateAccessToken(accessToken);

        //then
        Assertions.assertThat(response.isValid()).isTrue();
    }

    @DisplayName("주어진 액세스 토큰이 유효하지 않으면 false를 반환한다.")
    @Test
    public void validateAccessToken_WithInValidToken(){
        //given
        String accessToken = "Bearer notVaild.아무값이나 넣었어.sdfhsodjf";

        //when
        ValidateTokenResponseDto response = authService.validateAccessToken(accessToken);

        //then
        Assertions.assertThat(response.isValid()).isFalse();
    }

}
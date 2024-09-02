package com.munecting.api.domain.user.controller;


import com.munecting.api.domain.user.dto.request.LogoutRequestDto;
import com.munecting.api.domain.user.dto.request.RefreshTokenRequestDto;
import com.munecting.api.domain.user.dto.request.LoginRequestDto;
import com.munecting.api.domain.user.dto.response.UserTokenResponseDto;
import com.munecting.api.domain.user.service.AuthService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "auth", description = "유저 인증/인가 관련 API </br> <i> 담당자 : 김송은 </i>")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token/{userId}")
    @Operation(
            summary = "토큰 임시 발급 API (삭제 예정)")
    public ApiResponse<?> getToken(
            @PathVariable @Parameter(description = "user DB에 임의로 유저를 생성한 후, 생성되는 user id 값을 사용해주세요. ") Long userId
    ) {
        String accessToken = authService.getToken(userId);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), accessToken);
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인 또는 회원가입하기",
            description = "회원가입이 되어 있는 경우 로그인을 수행, 그렇지 않은 경우 회원가입을 수행합니다.")
    public ApiResponse<?> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto
    ) {
        UserTokenResponseDto dto = authService.getOrCreateUser(loginRequestDto);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), dto);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ApiResponse<?> logout(
            @RequestBody @Valid LogoutRequestDto logoutRequestDto
    ) {
        authService.logout(logoutRequestDto);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), null);
    }


    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급하기")
    public ApiResponse<?> refreshToken(
            @Valid RefreshTokenRequestDto refreshTokenRequestDto
    ) {
        UserTokenResponseDto dto = authService.refreshToken(refreshTokenRequestDto);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), dto);
    }
}

package com.munecting.api.domain.user.controller;


import com.munecting.api.domain.user.dto.UserRegisterDto;
import com.munecting.api.domain.user.dto.UserResponseDto;
import com.munecting.api.domain.user.service.UserService;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/jwt-test")
    public String test(@AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails.toString());
        return "jwt-test(success)";
    }


    /**
     * 앱 로그인 컨트롤러
     * request : userRegisterDto
     * response : UserResponseDto
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/login/register")
    public ApiResponse<?> registerUser(@RequestBody UserRegisterDto userRegisterDto){
        UserResponseDto responseDto = userService.register(userRegisterDto);
        return ApiResponse.onSuccess(Status.CREATED.getCode(),Status.CREATED.getMessage(),responseDto);
    }
}

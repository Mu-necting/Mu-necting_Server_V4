package com.munecting.api.domain.user.controller;


import com.munecting.api.domain.user.service.UserService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "user" , description = "user 관련 api")
public class UserController {
    private final UserService userService;

    @Operation(description = " access token을 통해 user id를 정상적으로 받아오는지 확인합니다. (삭제 예정)")
    @GetMapping("/test")
    public ApiResponse<?> test(
            @UserId Long userId
    ) {
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), userId);
    }

}

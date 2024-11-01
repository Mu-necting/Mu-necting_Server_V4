package com.munecting.api.domain.user.controller;


import com.munecting.api.domain.user.dto.request.UpdateProfileRequestDto;
import com.munecting.api.domain.user.dto.response.UpdateProfileResponseDto;
import com.munecting.api.domain.user.service.UserService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "user", description = "user 관련 api </br> <i> 담당자 : 김송은 </i>")
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    @Operation(
            summary = "@UserId 테스트용 (삭제 예정)",
            description = " access token을 통해 user id를 정상적으로 받아오는지 확인합니다. (삭제 예정)")
    public ApiResponse<?> test(
            @UserId Long userId
    ) {
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), userId);
    }

    //TODO: 엔드포인트에 userId 노출 제거
    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴")
    public ApiResponse<?> deleteUser(
            @PathVariable(name = "userId") Long userId
    ) {
        userService.deleteUser(userId);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), null);
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 업데이트")
    public ApiResponse<?> updateProfile(
            @UserId Long userId,

            @Schema(description = "새로운 프로필 이미지 파일", nullable = true, type = "string", format = "binary")
            @RequestPart(value = "profileImage", required = false)
            MultipartFile profileImage,

            @Schema(description = "새로운 닉네임", nullable = true)
            @RequestPart(value = "nickname", required = false)
            String nickname
    ){
        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto(nickname, profileImage);
        UpdateProfileResponseDto responseDto = userService.updateProfile(userId, requestDto);
        return ApiResponse.ok(responseDto);
    }
}

package com.munecting.api.domain.like.controller;

import com.munecting.api.domain.like.dto.response.AddTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.DeleteTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.GetLikedTrackResponseDto;
import com.munecting.api.domain.like.service.LikeService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
@Tag(name = "like", description = "Like 관련 api")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{trackId}/likes")
    @Operation(summary = "좋아요 누르기")
    public ApiResponse<?> addTrackLike(
            @PathVariable(name = "trackId") String trackId,
            @UserId Long userId
    ) {
        AddTrackLikeResponseDto dto = likeService.addTrackLike(trackId, userId);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), dto);
    }

    @DeleteMapping("/{trackId}/likes")
    @Operation(summary = "좋아요 취소")
    public ApiResponse<?> deleteTrackLike (
            @PathVariable(name = "trackId") String trackId,
            @UserId Long userId
    ) {
        DeleteTrackLikeResponseDto dto = likeService.deleteTrackLike(trackId, userId);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), dto);
    }
}

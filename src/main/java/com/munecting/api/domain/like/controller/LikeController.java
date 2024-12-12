package com.munecting.api.domain.like.controller;

import com.munecting.api.domain.like.dto.response.AddTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.DeleteTrackLikeResponseDto;
import com.munecting.api.domain.like.dto.response.GetLikePlaylistResponseDto;
import com.munecting.api.domain.like.dto.response.LikeResponseDto;
import com.munecting.api.domain.like.service.LikeService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/musics")
@Tag(name = "like", description = "Like 관련 api </br> <i> 담당자 : 김송은 </i>")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/liked")
    @Operation(summary = "좋아요한 음악 조회")
    public ApiResponse<?> getLikedTracks (
            @UserId Long userId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        // TODO: Like entity 변경으로 인한 로직 수정 필요
        GetLikePlaylistResponseDto dto = likeService.getLikedTracks(userId, cursor, size);
        return ApiResponse.ok(dto);
    }

    @PostMapping("/{musicId}/likes/toggle")
    @Operation(summary = "좋아요 토글")
    public ApiResponse<?> toggleTrackLike (
            @PathVariable(name = "musicId") String musicId,
            @UserId Long userId
    ){
        LikeResponseDto dto = likeService.toggleTrackLike(musicId, userId);
        return ApiResponse.ok(dto);
    }

}

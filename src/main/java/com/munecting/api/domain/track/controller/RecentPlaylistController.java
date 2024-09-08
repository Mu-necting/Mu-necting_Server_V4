package com.munecting.api.domain.track.controller;

import com.munecting.api.domain.track.dto.request.SaveRecentTracksRequestDto;
import com.munecting.api.domain.track.dto.response.GetRecentPlaylistResponseDto;
import com.munecting.api.domain.track.service.RecentPlaylistService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks/recent")
@Tag(name = "recent track", description = "최근 탐색한 플레이리스트 관련 api </br> <i> 담당자 : 김송은 </i>")
public class RecentPlaylistController {

    private final RecentPlaylistService recentPlaylistService;

    @PostMapping
    @Operation(summary = "최근 탐색한 음악 리스트 저장")
    public ApiResponse<?> saveRecentTracks(
            @UserId Long userId,
            @Valid @RequestBody SaveRecentTracksRequestDto requestDto
    ) {
        recentPlaylistService.saveRecentTracks(userId, requestDto);
        return ApiResponse.ok(null);
    }

    @GetMapping
    @Operation(summary = "최근 탐색한 음악 리스트 조회")
    public ApiResponse<?> getRecentTracks(
            @UserId Long userId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "100") int size
    ) {
        GetRecentPlaylistResponseDto dto = recentPlaylistService.getRecentTracks(userId, cursor, size);
        return ApiResponse.ok(dto);
    }
}

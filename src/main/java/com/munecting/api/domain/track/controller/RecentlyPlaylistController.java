package com.munecting.api.domain.track.controller;

import com.munecting.api.domain.track.dto.request.SaveRecentTrackRequestDto;
import com.munecting.api.domain.track.service.RecentlyPlaylistService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks/recent")
@Tag(name = "recent track", description = "최근 탐색한 플레이리스트 관련 api </br> <i> 담당자 : 김송은 </i>")
public class RecentlyPlaylistController {

    private final RecentlyPlaylistService recentlyPlaylistService;

    @PostMapping
    @Operation(summary = "최근 탐색한 음악 리스트 저장")
    public ApiResponse<?> saveRecentTracks(
            @UserId Long userId,
            @Valid @RequestBody SaveRecentTrackRequestDto requestDto
    ) {
        recentlyPlaylistService.saveRecentTracks(userId, requestDto);
        return ApiResponse.ok(null);
    }
}

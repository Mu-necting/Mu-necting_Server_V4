package com.munecting.api.domain.track.controller;

import com.munecting.api.domain.track.dto.response.GetTrackDetailsResponseDto;
import com.munecting.api.domain.track.service.TrackService;
import com.munecting.api.global.auth.user.UserId;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
@Tag(name = "track", description = "track 관련 기타 api")
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/{trackId}/details")
    @Operation(summary = "좋아요와 댓글 정보 조회")
    public ApiResponse<?> getTrackDetails(
            @PathVariable(name = "trackId") String trackId,
            @UserId Long userId
    ) {
        GetTrackDetailsResponseDto dto = trackService.getTrackDetails(trackId, userId);
        return ApiResponse.ok(dto);
    }
}

package com.munecting.api.domain.playListMusic.controller;

import com.munecting.api.domain.playListMusic.dto.requestDto.PlaylistMusicPlayOrderUpdateRequestDto;
import com.munecting.api.domain.playListMusic.dto.requestDto.PlaylistMusicRequestDto;
import com.munecting.api.domain.playListMusic.service.PlaylistMusicService;
import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlistMusic")
@Tag(name = "playlistMusic", description = "playlist 속 노래 관련 api")
public class PlaylistMusicController {

    private final PlaylistMusicService playlistMusicService;

    @PostMapping("/")
    @Operation(summary = "playlist에 노래 추가하기")
    public ApiResponse<?> addMusic(
            @RequestBody PlaylistMusicRequestDto playlistMusicRequestDto) {
        String trackId = playlistMusicService.addMusic(playlistMusicRequestDto);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), trackId);
    }

    @GetMapping("/{playlistId}")
    @Operation(summary = "playlist 속 노래 조회하기", description = "cursor 파라미터의 경우, cursor + 1번째부터 데이터를 조회하는데 사용됩니다.")
    public ApiResponse<?> getMusic(
            @PathVariable ("playlistId") Long playlistId,
            @RequestParam (name = "cursor") Long cursor,
            @RequestParam (name = "limit") int limit) {
        PagedResponseDto<MusicResponseDto> musicResponseDtoList = playlistMusicService.getMusic(playlistId, cursor, limit);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), musicResponseDtoList);
    }

    @DeleteMapping("/")
    @Operation(summary = "playlist에서 노래 삭제하기")
    public ApiResponse<?> deleteMusic(
            @RequestBody PlaylistMusicRequestDto playlistMusicRequestDto) {
        String trackId = playlistMusicService.deleteMusicFromPlaylist(playlistMusicRequestDto);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), trackId);
    }

    @PatchMapping("/{playlistId}")
    @Operation(summary = "playlist 속 노래 순서 변경하기")
    public ApiResponse<?> updatePlayOrder(
            @PathVariable ("playlistId") Long playlistId,
            @RequestBody PlaylistMusicPlayOrderUpdateRequestDto playlistMusicPlayOrderUpdateRequestDto) {
        Long id = playlistMusicService.updatePlayOrder(playlistId, playlistMusicPlayOrderUpdateRequestDto);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), id);
    }
}

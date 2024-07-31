package com.munecting.api.domain.playList.controller;

import com.munecting.api.domain.playList.dto.PlaylistResponseDto;
import com.munecting.api.domain.playList.service.PlaylistService;
import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlist")
@Tag(name = "playlist", description = "playlist 관련 api")
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/")
    @Operation(summary = "playlist 생성하기")
    public ApiResponse<?> createPlaylist(
            @RequestParam String playlistName,
            @RequestParam Long userId) { //userId는 추후 인증 과정에서 로그인한 사용자 정보 추출 로직 작성되면 삭제할 예정
        Long id = playlistService.createPlaylist(playlistName, userId);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), id);
    }

    @GetMapping("/all")
    @Operation(summary = "내 playlist 전체 조회하기")
    public ApiResponse<?> getMyPlaylist(
            @RequestParam Long userId, //userId는 추후 인증 과정에서 로그인한 사용자 정보 추출 로직 작성되면 삭제할 예정
            @RequestParam Long cursor,
            @RequestParam int limit) {
        List<PlaylistResponseDto> playlistResponseDtoList = playlistService.getMyPlaylist(userId, cursor, limit);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), playlistResponseDtoList);
    }

    @PatchMapping("/")
    @Operation(summary = "playlist 이름 수정하기")
    public ApiResponse<?> modifyPlaylistName(
            @RequestParam Long playlistId,
            @RequestParam String playlistName) {
        Long id = playlistService.modifyPlaylistName(playlistId, playlistName);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), id);
    }

    @DeleteMapping("/")
    @Operation(summary = "playlist 삭제하기")
    public ApiResponse<?> deletePlaylist(
            @RequestParam Long playlistId) {
        Long id = playlistService.deletePlaylist(playlistId);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), id);
    }


}

package com.munecting.api.domain.uploadedMusic.controller;

import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.uploadedMusic.dto.MusicRequestDto;
import com.munecting.api.domain.uploadedMusic.dto.UploadedMusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.uploadedMusic.service.MusicService;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/musics")
@Tag(name = "musics", description = "음악 관련 api")
public class MusicController {

    private final MusicService musicService;

    @PostMapping("")
    @Operation(summary = "음악 업로드 하기")
    public ApiResponse<?> uploadMusic(
           @Valid @RequestBody MusicRequestDto musicRequestDto) {
        Long id = musicService.uploadMusic(musicRequestDto);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), id);
    }

    @GetMapping("")
    @Operation(summary = "음악 조회하기")
    public ApiResponse<?> getUploadedMusic(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Integer radius) {
        List<UploadedMusicResponseDto> musicResponseDtoList = musicService.getUploadedMusics(latitude, longitude, radius);
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), musicResponseDtoList);
    }

}
package com.munecting.api.domain.spotify.controller;

import com.munecting.api.domain.spotify.dto.AlbumResponseDto;
import com.munecting.api.domain.spotify.dto.ArtistResponseDto;
import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spotify")
@Tag(name = "spotify", description = "spotify 관련 api")
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/search/tracks/{keyword}")
    @Operation(summary = "키워드로 트랙 검색하기")
    public ApiResponse<?> searchTracks(
            @PathVariable("keyword") String keyword,
            @RequestParam Integer limit,
            @RequestParam Integer offset
            ) {
        List<MusicResponseDto> musicResponseDtoList = spotifyService.searchTracks(keyword, limit, offset);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.CREATED.getMessage(), musicResponseDtoList);
    }

    @GetMapping("/search/albums/{keyword}")
    @Operation(summary = "키워드로 앨범 검색하기")
    public ApiResponse<?> searchAlbums(
            @PathVariable("keyword") String keyword,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<AlbumResponseDto> albumResponseDtoList = spotifyService.searchAlbums(keyword, limit, offset);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.CREATED.getMessage(), albumResponseDtoList);
    }

    @GetMapping("/search/artists/{keyword}")
    @Operation(summary = "키워드로 아티스트 검색하기")
    public ApiResponse<?> searchArtists(
            @PathVariable("keyword") String keyword,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<ArtistResponseDto> artistResponseDtoList = spotifyService.searchArtists(keyword, limit, offset);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.CREATED.getMessage(), artistResponseDtoList);
    }

    @GetMapping("/albums/{id}/tracks")
    @Operation(summary = "앨범 아이디로 노래 조회하기")
    public ApiResponse<?> getTracksByAlbumId(
            @PathVariable("id") String albumId,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<MusicResponseDto> musicResponseDtoList = spotifyService.getTracksByAlbumId(albumId, limit, offset);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.CREATED.getMessage(), musicResponseDtoList);
    }

    @GetMapping("/artists/{id}/albums")
    @Operation(summary = "아티스트 아이디로 앨범 조회하기")
    public ApiResponse<?> getAlbumsByArtistId(
            @PathVariable("id") String albumId,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<AlbumResponseDto> albumResponseDtoList = spotifyService.getAlbumsByArtistId(albumId, limit, offset);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.CREATED.getMessage(), albumResponseDtoList);
    }

    @GetMapping("/tracks/{id}")
    @Operation(summary = "노래 아이디로 정보 조회하기")
    public ApiResponse<?> getAlbumsByArtistId(
            @PathVariable("id") String trackId
    ) {
        MusicResponseDto musicResponseDto = spotifyService.getTrack(trackId);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.CREATED.getMessage(), musicResponseDto);
    }

}

package com.munecting.api.domain.spotify.controller;

import com.munecting.api.domain.spotify.dto.response.AlbumResponseDto;
import com.munecting.api.domain.spotify.dto.response.ArtistResponseDto;
import com.munecting.api.domain.spotify.dto.response.MusicResponseDto;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.global.common.dto.response.ApiResponse;
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
@RequestMapping("/api/spotify")
@Tag(name = "spotify", description = "spotify 관련 api </br> <i> 담당자 : 전민주 </i>")
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/search/tracks")
    @Operation(summary = "키워드로 트랙 검색하기")
    public ApiResponse<?> searchTracks(
            @RequestParam("keyword") String keyword,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<MusicResponseDto> musicResponseDtoList = spotifyService.searchTracks(keyword, limit, offset);
        return ApiResponse.ok(musicResponseDtoList);
    }

    @GetMapping("/search/albums")
    @Operation(summary = "키워드로 앨범 검색하기")
    public ApiResponse<?> searchAlbums(
            @RequestParam("keyword") String keyword,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<AlbumResponseDto> albumResponseDtoList = spotifyService.searchAlbums(keyword, limit, offset);
        return ApiResponse.ok(albumResponseDtoList);
    }

    @GetMapping("/search/artists")
    @Operation(summary = "키워드로 아티스트 검색하기")
    public ApiResponse<?> searchArtists(
            @RequestParam("keyword") String keyword,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<ArtistResponseDto> artistResponseDtoList = spotifyService.searchArtists(keyword, limit, offset);
        return ApiResponse.ok(artistResponseDtoList);
    }

    @GetMapping("/albums/{id}/tracks")
    @Operation(summary = "앨범 아이디로 노래 조회하기")
    public ApiResponse<?> getTracksByAlbumId(
            @PathVariable("id") String albumId,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<MusicResponseDto> musicResponseDtoList = spotifyService.getTracksByAlbumId(albumId, limit, offset);
        return ApiResponse.ok(musicResponseDtoList);
    }

    @GetMapping("/artists/{id}/albums")
    @Operation(summary = "아티스트 아이디로 앨범 조회하기")
    public ApiResponse<?> getAlbumsByArtistId(
            @PathVariable("id") String albumId,
            @RequestParam Integer limit,
            @RequestParam Integer offset
    ) {
        List<AlbumResponseDto> albumResponseDtoList = spotifyService.getAlbumsByArtistId(albumId, limit, offset);
        return ApiResponse.ok(albumResponseDtoList);
    }

    @GetMapping("/tracks/{id}")
    @Operation(summary = "노래 아이디로 정보 조회하기")
    public ApiResponse<?> getAlbumsByArtistId(
            @PathVariable("id") String trackId
    ) {
        MusicResponseDto musicResponseDto = spotifyService.getTrack(trackId);
        return ApiResponse.ok(musicResponseDto);
    }
}

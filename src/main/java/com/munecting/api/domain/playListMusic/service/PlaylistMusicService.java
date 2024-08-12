package com.munecting.api.domain.playListMusic.service;

import com.munecting.api.domain.playList.entity.Playlist;
import com.munecting.api.domain.playList.service.PlaylistService;
import com.munecting.api.domain.playListMusic.dao.PlaylistMusicRepository;
import com.munecting.api.domain.playListMusic.dto.requestDto.PlaylistMusicPlayOrderUpdateRequestDto;
import com.munecting.api.domain.playListMusic.dto.requestDto.PlaylistMusicRequestDto;
import com.munecting.api.domain.playListMusic.entity.PlaylistMusic;
import com.munecting.api.domain.playListMusic.entity.PlaylistMusicId;
import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.common.dto.response.PagedResponseDto;
import com.munecting.api.global.common.dto.response.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlaylistMusicService {

    private final PlaylistMusicRepository playlistMusicRepository;
    private final PlaylistService playlistService;
    private final SpotifyService spotifyService;

    @Transactional
    public String addMusic(PlaylistMusicRequestDto playlistMusicRequestDto) {
        Long playlistId = playlistMusicRequestDto.getPlaylistId();
        String trackId = playlistMusicRequestDto.getTrackId();
        playlistService.findPlaylistById(playlistId);
        spotifyService.getTrack(trackId);
        PlaylistMusicId playlistMusicId = PlaylistMusicId.toEntity(playlistId, trackId);
        PlaylistMusic playlistMusic = PlaylistMusic.toEntity(playlistMusicId);
        long order = playlistMusicRepository.countByPlaylistId(playlistId);
        playlistMusic.updatePlayOrder(order + 1);
        savePlaylistMusicEntity(playlistMusic);
        return trackId;
    }

    @Transactional
    public PagedResponseDto<MusicResponseDto> getMusic(Long playlistId, Long cursor, int limit) {
        Playlist playlist = playlistService.findPlaylistById(playlistId);
        cursor = cursor == null ? 0 : cursor;
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "playOrder"));
        Page<PlaylistMusic> playlistMusicList = playlistMusicRepository.findByPlaylistIdAndOrderGreaterThan(playlistId, cursor, pageable);
        List<String> trackIdList = playlistMusicList.stream()
                .map(playlistMusic -> playlistMusic.getId().getTrackId())
                .toList();
        List<MusicResponseDto> musicResponseDtoList = trackIdList.stream()
                .map(spotifyService::getTrack)
                .collect(Collectors.toList());
        Page<MusicResponseDto> musicResponseDtoPage = new PageImpl<>(musicResponseDtoList, pageable, playlistMusicList.getTotalElements());

        return new PagedResponseDto<>(musicResponseDtoPage);
    }

    public Long updatePlayOrder(Long playlistId, PlaylistMusicPlayOrderUpdateRequestDto playlistMusicPlayOrderUpdateRequestDto) {
        Playlist playlist = playlistService.findPlaylistById(playlistId);
        playlistMusicPlayOrderUpdateRequestDto.getPlaylistMusicPlayOrderRequestDtoList().stream()
                .forEach(playlistMusicPlayOrderRequestDto -> {
                    String trackId = playlistMusicPlayOrderRequestDto.getTrackId();
                    Long playOrder = playlistMusicPlayOrderRequestDto.getPlayOrder();
                    PlaylistMusicId playlistMusicId = PlaylistMusicId.toEntity(playlistId, trackId);
                    PlaylistMusic playlistMusic = getPlaylistById(playlistMusicId);
                    playlistMusic.updatePlayOrder(playOrder);
                    playlistMusicRepository.save(playlistMusic);
                });
        return playlist.getId();
    }

    @Transactional
    public String deleteMusicFromPlaylist(PlaylistMusicRequestDto playlistMusicRequestDto) {
        Long playlistId = playlistMusicRequestDto.getPlaylistId();
        String trackId = playlistMusicRequestDto.getTrackId();
        PlaylistMusicId playlistMusicId = PlaylistMusicId.toEntity(playlistId, trackId);
        PlaylistMusic playlistMusic = getPlaylistById(playlistMusicId);
        deletePlaylistMusicEntity(playlistMusic);
        return trackId;
    }

    public void savePlaylistMusicEntity(PlaylistMusic playlistMusic) {
        playlistMusicRepository.save(playlistMusic);
    }

    public void deletePlaylistMusicEntity(PlaylistMusic playlistMusic) {
        playlistMusicRepository.delete(playlistMusic);
    }

    public PlaylistMusic getPlaylistById (PlaylistMusicId playlistMusicId) {
        return playlistMusicRepository.findById(playlistMusicId)
                .orElseThrow(() -> new EntityNotFoundException(Status.PLAY_LIST_MUSIC_NOT_FOUND));
    }

}

package com.munecting.api.domain.playList.service;

import com.munecting.api.domain.playList.dao.PlaylistRepository;
import com.munecting.api.domain.playList.dto.PlaylistResponseDto;
import com.munecting.api.domain.playList.entity.Playlist;
import com.munecting.api.domain.playListMusic.entity.PlaylistMusic;
import com.munecting.api.global.common.dto.response.GeneralException;
import com.munecting.api.global.common.dto.response.Status;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Transactional
    public Long createPlaylist(String playlistName, Long userId) {
        Playlist playlist = Playlist.toEntity(playlistName, userId);
        Long id = save(playlist);
        return id;
    }

    @Transactional
    public List<PlaylistResponseDto> getMyPlaylist(Long userId, Long cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "id"));
        List<Playlist> playlists = playlistRepository.findByUserIdAndIdAfter(userId, cursor, pageable);
        log.info(playlists.size() + " ");
        return playlists.stream().map(
                playlist -> Playlist.toDto(playlist))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long modifyPlaylistName(Long playlistId, String playlistName) {
        Playlist playlist = findPlaylistById(playlistId);
        playlist.updateName(playlistName);
        save(playlist);
        return playlist.getId();
    }

    @Transactional
    public Long deletePlaylist(Long playlistId) {
        Playlist playlist = findPlaylistById(playlistId);
        delete(playlist);
        return playlist.getId();
    }

    public Long save(Playlist playlist) {
        return playlistRepository.save(playlist).getId();
    }

    public void delete(Playlist playlist) {
        playlistRepository.delete(playlist);
        // playlistmusic의 delete도 처리
    }

    public Playlist findPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new GeneralException(Status.PLAY_LIST_NOT_FOUND));
    }

}

package com.munecting.api.domain.uploadedMusic.service;

import com.munecting.api.domain.spotify.dto.response.MusicResponseDto;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.uploadedMusic.dto.request.MusicRequestDto;
import com.munecting.api.domain.uploadedMusic.dto.response.UploadedMusicIdResponseDto;
import com.munecting.api.domain.uploadedMusic.dto.response.UploadedMusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicService {

    private final UploadedMusicRepository uploadedMusicRepository;
    private final SpotifyService spotifyService;

    @Transactional
    public UploadedMusicIdResponseDto uploadMusic(MusicRequestDto musicRequestDto) {
        spotifyService.getTrack(musicRequestDto.trackId());
        UploadedMusic uploadedMusic = UploadedMusic.toEntity(musicRequestDto);
        return saveUploadMusicEntity(uploadedMusic);
    }

    @Transactional(readOnly = true)
    public List<UploadedMusicResponseDto> getUploadedMusics(Double latitude, Double longitude, Integer radius) {
        List<UploadedMusic> uploadedMusics = getUploadedMusicByLocationAndRadius(latitude, longitude, radius);
        List<UploadedMusicResponseDto> uploadedMusicResponseDtos
                = uploadedMusics.stream()
                .map(uploadedMusic -> {
                    MusicResponseDto musicResponseDto = spotifyService.getTrack(uploadedMusic.getTrackId());
                    User user = null; // 인증 부분 완료되면 수정 예정
                    return UploadedMusicResponseDto.of(uploadedMusic, musicResponseDto);
                }).collect(Collectors.toList());
        return uploadedMusicResponseDtos;
    }

    private UploadedMusicIdResponseDto saveUploadMusicEntity(UploadedMusic uploadedMusic) {
        Long id = uploadedMusicRepository.save(uploadedMusic).getId();
        return UploadedMusicIdResponseDto.of(id);
    }

    @Transactional(readOnly = true)
    public List<UploadedMusic> getUploadedMusicByLocationAndRadius(Double latitude, Double longitude, Integer radius) {
        return uploadedMusicRepository.findUploadedMusicByLocationAndRadius(latitude, longitude, radius);
    }
}

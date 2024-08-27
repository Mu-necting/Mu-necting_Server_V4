package com.munecting.api.domain.uploadedMusic.service;

import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.uploadedMusic.dto.MusicRequestDto;
import com.munecting.api.domain.uploadedMusic.dto.UploadedMusicResponseDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicService {

    private final UploadedMusicRepository uploadedMusicRepository;
    private final SpotifyService spotifyService;

    public Long uploadMusic(MusicRequestDto musicRequestDto) {
        spotifyService.getTrack(musicRequestDto.getTrackId());
        UploadedMusic uploadedMusic = UploadedMusic.toEntity(musicRequestDto);
        return saveUploadMusicEntity(uploadedMusic);
    }

    public List<UploadedMusicResponseDto> getUploadedMusics(Double latitude, Double longitude, Integer radius) {
        List<UploadedMusic> uploadedMusics = getUploadedMusicByLocationAndRadius(latitude, longitude, radius);
        List<UploadedMusicResponseDto> uploadedMusicResponseDtos
                = uploadedMusics.stream()
                .map(uploadedMusic -> {
                    MusicResponseDto musicResponseDto = spotifyService.getTrack(uploadedMusic.getTrackId());
                    User user = null; // 인증 부분 완료되면 수정 예정
                    return UploadedMusicResponseDto.toDto(uploadedMusic, musicResponseDto);
                }).collect(Collectors.toList());
        return uploadedMusicResponseDtos;
    }

    private Long saveUploadMusicEntity(UploadedMusic uploadedMusic) {
        return uploadedMusicRepository.save(uploadedMusic).getId();
    }

    private List<UploadedMusic> getUploadedMusicByLocationAndRadius(Double latitude, Double longitude, Integer radius) {
        return uploadedMusicRepository.findUploadedMusicByLocationAndRadius(latitude, longitude, radius);
    }

    public void deleteUploadedMusicsByUserId(Long userId) {
        uploadedMusicRepository.deleteByUserId(userId);
    }
}

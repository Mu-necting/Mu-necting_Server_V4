package com.munecting.api.domain.track.service;

import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.track.dao.RecentPlaylistRepository;
import com.munecting.api.domain.track.domain.RecentlyPlayedTrack;
import com.munecting.api.domain.track.dto.request.SaveRecentTracksRequestDto;
import com.munecting.api.domain.track.dto.request.RecentTrackInfo;
import com.munecting.api.domain.track.dto.response.GetRecentPlaylistResponseDto;
import com.munecting.api.domain.track.dto.response.RecentlyPlayedTrackResponseDto;
import com.munecting.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecentPlaylistService {

    private final RecentPlaylistRepository recentPlaylistRepository;
    private final SpotifyService spotifyService;
    private final UserService userService;
    private static final int maxRecentTracks = 200;

    @Transactional
    public void saveRecentTracks(Long userId, SaveRecentTracksRequestDto requestDto) {
        userService.validateUserExists(userId);
        validateTracksExist(requestDto.tracks());

        List<String> sortedTrackIds = extractSortedTrackIds(requestDto.tracks());
        deleteToSaveNewRecentTracks(userId, sortedTrackIds);
        saveNewTracks(userId, sortedTrackIds);
    }

    private void validateTracksExist(List<RecentTrackInfo> tracks) {
        tracks.forEach(trackInfo -> spotifyService.validateTrackId(trackInfo.trackId()));
    }

    /**
     * TrackInfo에서 트랙 아이디를 추출한 후 탐색한 순서를 기준으로 정렬합니다.
     * @param trackInfos 탐색 순서와 함께 제공된 track id 리스트
     * @return 탐색 순서대로 정렬되고 중복이 제거된 track id 리스트
     */
    private List<String> extractSortedTrackIds(List<RecentTrackInfo> trackInfos) {
        return trackInfos.stream()
                .sorted(Comparator.comparingInt(RecentTrackInfo::order))
                .map(RecentTrackInfo::trackId)
                .distinct()
                .collect(Collectors.toList());
    }

    private void deleteToSaveNewRecentTracks(Long userId, List<String> sortedTrackIds) {
        deleteOverlappedRecords(userId, sortedTrackIds);
        deleteOverflowRecords(userId, sortedTrackIds.size());
    }

    /**
     * 새롭게 저장하려는 탐색 기록과 중복되는 기존 기록이 있다면 이를 삭제합니다.
     * @param userId 유저 아이디
     * @param newTrackIds 새로 탐색한 트랙의 아이디 리스트
     */
    private void deleteOverlappedRecords(Long userId, List<String> newTrackIds) {
        List<String> existingTrackIds = recentPlaylistRepository.findTrackIdsByUserId(userId);

        List<String> trackIdsToDelete = existingTrackIds.stream()
                .filter(newTrackIds::contains)
                .collect(Collectors.toList());

        if (!trackIdsToDelete.isEmpty()) {
            recentPlaylistRepository.deleteAllByUserIdAndTrackIds(userId, trackIdsToDelete);
        }
    }

    /**
     * 기록 가능한 최대 트랙 수를 초과하면, 기존의 오래된 기록을 삭제합니다.
     * @param userId 유저 아이디
     * @param newTracksCount 새로 탐색한 트랙의 개수
     */
    private void deleteOverflowRecords(Long userId, int newTracksCount) {
        int currentTrackCount = recentPlaylistRepository.countByUserId(userId);
        int totalTracksAfterAddition = currentTrackCount + newTracksCount;

        if (totalTracksAfterAddition > maxRecentTracks) {
            int tracksToDelete = totalTracksAfterAddition - maxRecentTracks;
            deleteOldestRecords(userId, tracksToDelete);
        }
    }

    private void deleteOldestRecords(Long userId, int count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<String> trackIdsToDelete = recentPlaylistRepository.findOldestTracks(userId, pageRequest);
        recentPlaylistRepository.deleteAllByUserIdAndTrackIds(userId, trackIdsToDelete);
    }

    private void saveNewTracks(Long userId, List<String> sortedTrackIdsByASC) {
        LocalDateTime now = LocalDateTime.now();
        AtomicInteger counter = new AtomicInteger();

        List<RecentlyPlayedTrack> newTracks = sortedTrackIdsByASC.stream()
                .map(trackId -> {
                    RecentlyPlayedTrack track = RecentlyPlayedTrack.toEntity(trackId, userId);
                    // 트랙의 생성 시간을 순차적으로 설정
                    track.setCreatedAt(now.plusSeconds(counter.getAndIncrement()));
                    return track;
                })
                .collect(Collectors.toList());

        recentPlaylistRepository.saveAll(newTracks);
    }

    @Transactional(readOnly = true)
    public GetRecentPlaylistResponseDto getRecentTracks(Long userId, Long cursor, int size) {
        userService.validateUserExists(userId);

        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<RecentlyPlayedTrack> playedTracks = getPlayedTrackSlice(userId, cursor, pageRequest);

        List<RecentlyPlayedTrackResponseDto> trackInfos = playedTracks.stream()
                .map(playedTrack ->
                        spotifyService.getRecentlyPlayedTrack(playedTrack.getTrackId(), playedTrack.getId()))
                .collect(Collectors.toList());

        return GetRecentPlaylistResponseDto.of(playedTracks.isEmpty(), playedTracks.hasNext(), trackInfos);
    }

    private Slice<RecentlyPlayedTrack> getPlayedTrackSlice(Long userId, Long cursor, PageRequest pageRequest) {
        if (cursor == null) {
            return recentPlaylistRepository.findByUserId(userId, pageRequest);
        } else {
            return recentPlaylistRepository.findByUserId(userId, cursor, pageRequest);
        }
    }
}
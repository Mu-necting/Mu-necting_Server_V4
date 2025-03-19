package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.TrackLikeRepository;
import com.munecting.api.domain.like.dao.UserTrackLikeRepository;
import com.munecting.api.domain.like.dto.response.*;
import com.munecting.api.domain.like.entity.TrackLike;
import com.munecting.api.domain.like.entity.UserTrackLike;
import com.munecting.api.domain.spotify.service.SpotifyService;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.error.exception.ConflictException;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class LikeServiceTest {

    @Autowired
    private UserTrackLikeRepository userTrackLikeRepository;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UploadedMusicRepository uploadedMusicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackLikeRepository trackLikeRepository;

    @MockBean
    private SpotifyService spotifyService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        trackLikeRepository.deleteAllInBatch();
        uploadedMusicRepository.deleteAllInBatch();
        userTrackLikeRepository.deleteAllInBatch();
    }


    @DisplayName("좋아요를 누른 음악을 최신순으로 조회한다.")
    @Test
    public void getLikedTracks(){
        //given
        var userId = saveUser("뮤넥터_m");
        var likeTrackIds = saveTrackLikeStatuses(userId);
        when(spotifyService.getLikeTrackInfoMap(anyList()))
                .thenReturn(Map.of(
                        likeTrackIds.get(0), mockTrackInfo(1),
                        likeTrackIds.get(1), mockTrackInfo(2),
                        likeTrackIds.get(2), mockTrackInfo(3))
        );

        //when
        GetLikePlaylistResponseDto response = likeService.getLikedTracks(userId, null, 5);

        //then
        List<LikeTrackResponseDto> likeTrackResponseDtos = response.likePlaylist();
        assertThat(likeTrackResponseDtos)
                .hasSize(3);

        List<TrackResponseDto> trackResponseDtos = likeTrackResponseDtos.stream().map(LikeTrackResponseDto::track).toList();
        assertThat(trackResponseDtos)
                .hasSize(3)
                .extracting("trackId", "trackTitle")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("trackId1", "name"),
                        Tuple.tuple("trackId2", "name"),
                        Tuple.tuple("trackId3", "name")
                );
    }

    @DisplayName("유저가 좋아요를 누르지 않았던 음악이라면 좋아요를 추가한다.")
    @Test
    public void toggleTrackLike_BeforeAddLike(){
        //given
        long userId = saveUser("뮤넥터_m");

        String trackId = "trackId";
        doNothing().when(spotifyService).validateTrackExists(trackId);

        //when
        LikeResponseDto response = likeService.toggleTrackLike(trackId, userId);

        //then
        assertThat(response.likeCount()).isEqualTo(1);
        assertThat(response.userLiked()).isTrue();
    }

    @DisplayName("유저가 좋아요를 눌렀던 음악이라면 좋아요를 취소한다.")
    @Test
    public void toggleTrackLike_AfterAddLike(){
        //given
        long userId = saveUser("뮤넥터_m");
        String trackId = "trackId";

        UserTrackLike userTrackLike = UserTrackLike.toEntity(userId, trackId, true);
        userTrackLikeRepository.save(userTrackLike);

        TrackLike trackLike = TrackLike.toEntity(1, trackId);
        trackLikeRepository.save(trackLike);

        doNothing().when(spotifyService).validateTrackExists(trackId);

        //when
        LikeResponseDto response = likeService.toggleTrackLike(trackId, userId);

        //then
        assertThat(response.likeCount()).isEqualTo(0);
        assertThat(response.userLiked()).isFalse();
    }

    @DisplayName("한 유저가 동시에 좋아요를 요청할 때, 성공/실패에 따른 최종 좋아요 상태를 검증한다.")
    @Test
    public void toggleTrackLike_ConcurrentAccess_withOneUser() throws InterruptedException {
        // given
        long userId = saveUser("뮤넥터_m");
        String trackId = "trackId";

        doNothing().when(spotifyService).validateTrackExists(trackId);

        int threadCount = 100; // 동시에 요청을 보낼 스레드 개수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger conflictExceptionCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    likeService.toggleTrackLike(trackId, userId);
                } catch (ConflictException e){
                    conflictExceptionCount.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        // then
        if (conflictExceptionCount.get() == threadCount) { // 모든 스레드가 실패
            assertThat(userTrackLikeRepository.findByTrackIdAndUserId(trackId, userId)).isEmpty();
        } else {
            Integer likeCount = trackLikeRepository.findByTrackId(trackId).get().getLikeCount();
            if (conflictExceptionCount.get() %2 == 0) { // 성공한 스레드 개수가 짝수
                assertThat(likeCount).isZero();
                assertThat(userTrackLikeRepository.findByTrackIdAndUserId(trackId, userId).get().isLiked()).isFalse();
            }
            if (conflictExceptionCount.get() %2 == 1){ // 성공한 스레드 개수가 홀수
                assertThat(likeCount).isOne();
                assertThat(userTrackLikeRepository.findByTrackIdAndUserId(trackId, userId).get().isLiked()).isTrue();
            }
        }
    }

    @DisplayName("여러 유저가 동시에 좋아요를 요청했을 때 성공한 요청 수와 트랙 좋아요 수가 일치한다.")
    @Test
    public void toggleTrackLike_ConcurrentAccess_withManyUsers() throws InterruptedException{
        // given
        long startUserId = savesUsers();

        String trackId = "trackId";
        doNothing().when(spotifyService).validateTrackExists(trackId);

        int threadCount = 10; // 동시에 요청을 보낼 스레드 개수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger conflictExceptionCount = new AtomicInteger(0);

        // when
        for (int i = (int) startUserId; i < threadCount + startUserId; i++) {
            long userId = i;

            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    likeService.toggleTrackLike(trackId, userId);
                } catch (ConflictException e){
                    conflictExceptionCount.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        //then
        int successCount = 10 - conflictExceptionCount.get();
        assertThat(trackLikeRepository.findByTrackId(trackId).get().getLikeCount()).isEqualTo(successCount);
    }

    private TrackResponseDto mockTrackInfo(int index) {
        return TrackResponseDto.of(
                new Track.Builder()
                        .setId("trackId"+index)
                        .setName("name")
                        .setArtists(new ArtistSimplified.Builder().setName("도경수").build())
                        .setPreviewUrl("previewUrl")
                        .setAlbum(new AlbumSimplified.Builder()
                                .setImages(new Image.Builder().setHeight(1).setUrl("imgUrl").setWidth(1).build())
                                .build()
                        ).build()
                , List.of(new LikeArtistResponseDto("도경수"))
        );
    }

    private List<String> saveTrackLikeStatuses(long userId) {
        UserTrackLike likedTrack_1 = UserTrackLike.toEntity(userId, "trackId1", true);
        UserTrackLike likedTrack_2 = UserTrackLike.toEntity(userId, "trackId2", true);
        UserTrackLike likedTrack_3 = UserTrackLike.toEntity(userId, "trackId3", true);
        UserTrackLike notLikedTrack_4 = UserTrackLike.toEntity(userId, "trackId4", false);
        userTrackLikeRepository.saveAll(List.of(likedTrack_1, likedTrack_2, likedTrack_3, notLikedTrack_4));

        return List.of(likedTrack_1.getTrackId(), likedTrack_2.getTrackId(), likedTrack_3.getTrackId());
    }

    private long savesUsers() {
        long[] userIds = IntStream.rangeClosed(1, 10)
                .mapToLong(i -> saveUser("뮤넥터_"+i))
                .toArray();

        return userIds[0];
    }

    private long saveUser(String nickname) {
        return userRepository.save(
                User.toEntity("소셜 아이디",nickname, Role.USER, SocialType.GOOGLE)
        ).getId();
    }

}

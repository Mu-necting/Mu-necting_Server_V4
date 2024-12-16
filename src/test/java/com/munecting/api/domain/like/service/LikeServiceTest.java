package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.TrackLikeRepository;
import com.munecting.api.domain.like.dao.UserTrackLikeRepository;
import com.munecting.api.domain.like.dto.response.LikeResponseDto;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.uploadedMusic.dto.request.MusicRequestDto;
import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.error.exception.ConflictException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

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

    @BeforeEach
    @Transactional
    void init() {
        long user1 = 1L;
        long user2 = 2L;

        createMusicLikeRequestUser(user1, "뮤넥터_111", "testSocialId1");
        createMusicLikeRequestUser(user2, "뮤넥터_222", "testSocialId2");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        trackLikeRepository.deleteAllInBatch();
        uploadedMusicRepository.deleteAllInBatch();
        userTrackLikeRepository.deleteAllInBatch();
    }

    @DisplayName("유저가 좋아요를 누르지 않았던 음악이라면 좋아요를 추가한다.")
    @Test
    public void toggleTrackLike_BeforeAddLike(){
        //given
        MusicRequestDto musicRequestDto = new MusicRequestDto(37.3012, 127.0356, "1mWdTewIgB3gtBM3TOSFhB", 7);
        UploadedMusic music = UploadedMusic.toEntity(1L, musicRequestDto);
        uploadedMusicRepository.save(music);

        //when
        LikeResponseDto response = likeService.toggleTrackLike("1mWdTewIgB3gtBM3TOSFhB", 2L);

        //then
        assertThat(response.likeCount()).isEqualTo(1);
        assertThat(response.userLiked()).isTrue();
    }

    @DisplayName("유저가 좋아요를 눌렀던 음악이라면 좋아요를 제거한다.")
    @Test
    public void toggleTrackLike_AfterAddLike(){
        //given
        MusicRequestDto musicRequestDto = new MusicRequestDto(37.3012, 127.0356, "1mWdTewIgB3gtBM3TOSFhB", 7);
        UploadedMusic music = UploadedMusic.toEntity(1L, musicRequestDto);
        uploadedMusicRepository.save(music);
        likeService.toggleTrackLike("trackId", 2L);

        //when
        LikeResponseDto response = likeService.toggleTrackLike("trackId", 2L);

        //then
        assertThat(response.likeCount()).isEqualTo(0);
        assertThat(response.userLiked()).isFalse();
    }

    @DisplayName("한 유저가 동시에 좋아요를 요청할 때, 성공/실패에 따른 최종 좋아요 상태를 검증한다.")
    @Test
    public void toggleTrackLike_ConcurrentAccess_withOneUser() throws InterruptedException {
        // given
        long musicUploadedUserId = 1L;
        long musicLikeRequestUserId = 2L;

        String trackId = "1mWdTewIgB3gtBM3TOSFhB";
        MusicRequestDto musicRequestDto = new MusicRequestDto(37.3012, 127.0356, trackId, 7);
        UploadedMusic music = UploadedMusic.toEntity(musicUploadedUserId, musicRequestDto);
        uploadedMusicRepository.save(music);

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
                    likeService.toggleTrackLike(trackId, musicLikeRequestUserId);
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
            assertThat(trackLikeRepository.findByTrackId(trackId).get().getLikeCount()).isZero();
            assertThat(userTrackLikeRepository.findByTrackIdAndUserId(trackId, musicLikeRequestUserId)).isEmpty();
        } else {
            Integer likeCount = trackLikeRepository.findByTrackId(trackId).get().getLikeCount();
            if (conflictExceptionCount.get() %2 == 0) { // 성공한 스레드 개수가 짝수
                assertThat(likeCount).isZero();
                assertThat(userTrackLikeRepository.findByTrackIdAndUserId(trackId, musicLikeRequestUserId).get().isLiked()).isFalse();
            }
            if (conflictExceptionCount.get() %2 == 1){ // 성공한 스레드 개수가 홀수
                assertThat(likeCount).isOne();
                assertThat(userTrackLikeRepository.findByTrackIdAndUserId(trackId, musicLikeRequestUserId).get().isLiked()).isTrue();
            }
        }
    }

    @DisplayName("여러 유저가 동시에 좋아요를 요청했을 때 성공한 요청 수와 트랙 좋아요 수가 일치한다.")
    @Test
    public void toggleTrackLike_ConcurrentAccess_withManyUsers() throws InterruptedException{
        // given
        long musicUploadedUserId = 1L;
        createMusicLikeRequestUsers();

        String trackId = "1mWdTewIgB3gtBM3TOSFhB";
        MusicRequestDto musicRequestDto = new MusicRequestDto(37.3012, 127.0356, trackId, 7);

        UploadedMusic music = UploadedMusic.toEntity(musicUploadedUserId, musicRequestDto);
        uploadedMusicRepository.save(music);


        int threadCount = 10; // 동시에 요청을 보낼 스레드 개수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger exceptionCount = new AtomicInteger(0);

        // when
        for (int i = 2; i < threadCount + 2; i++) {
            int finalI = i;
            executorService.submit(() -> {
                long musicLikeRequestUserId = musicUploadedUserId + finalI;
                readyLatch.countDown();
                try {
                    startLatch.await();
                    likeService.toggleTrackLike(trackId, musicLikeRequestUserId);
                } catch (ConflictException e){
                    exceptionCount.incrementAndGet();
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
        int successCount = 10 - exceptionCount.get();
        assertThat(trackLikeRepository.findByTrackId(trackId).get().getLikeCount()).isEqualTo(successCount);
    }

    private void createMusicLikeRequestUsers() {
        createMusicLikeRequestUser(3L, "뮤넥터_333", "testSocialId3");
        createMusicLikeRequestUser(4L, "뮤넥터_444", "testSocialId4");
        createMusicLikeRequestUser(5L, "뮤넥터_555", "testSocialId5");
        createMusicLikeRequestUser(6L, "뮤넥터_666", "testSocialId6");
        createMusicLikeRequestUser(7L, "뮤넥터_777", "testSocialId7");
        createMusicLikeRequestUser(8L, "뮤넥터_888", "testSocialId8");
        createMusicLikeRequestUser(9L, "뮤넥터_999", "testSocialId9");
        createMusicLikeRequestUser(10L, "뮤넥터_123", "testSocialId10");

    }

    private void createMusicLikeRequestUser(long id, String nickname, String socialId) {
        userRepository.save(
                User.builder()
                        .id(id)
                        .role(Role.USER)
                        .socialType(SocialType.GOOGLE)
                        .nickname(nickname)
                        .socialId(socialId)
                        .build()
        );
    }

}

package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LikeServiceTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserRepository userRepository;

    private static final int NUMBER_OF_THREAD = 50;

    @BeforeEach
    @Transactional
    void init() {
        long userId = new AtomicLong(1).get();
        System.out.println("userId:" + userId);

        userRepository.save(
                User.builder()
                        .id(userId)
                        .role(Role.USER)
                        .socialType(SocialType.GOOGLE)
                        .socialId("testSocialId")
                        .build()
        );
    }

    @Test
    void 멀티_스레드_환경에서_동일한_유저가_좋아요를_여러_번_눌러도_한_번만_증가한다() throws InterruptedException {

        // given
        long userId = new AtomicLong(1).get();
        final String trackId = "2VdSktBqFfkW7y6q5Ik4Z4";

        final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
        final CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREAD);

        // when
        for (int i = 0; i < NUMBER_OF_THREAD; i++) {
            executorService.execute(
                    () -> {
                        try {
                            likeService.addTrackLike(trackId, userId);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    });
        }
        latch.await();
        executorService.shutdown();

        // then
        int actualTrackCount = likeRepository.countByTrackId(trackId);
        assertEquals(1, actualTrackCount);
    }
}

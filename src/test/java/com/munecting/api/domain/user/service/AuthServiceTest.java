package com.munecting.api.domain.user.service;

import com.munecting.api.domain.oidc.dto.OidcUserInfo;
import com.munecting.api.domain.oidc.service.OidcService;
import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.dto.request.LoginRequestDto;
import com.munecting.api.domain.user.dto.response.ValidateTokenResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.error.exception.NicknameException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @MockBean
    private OidcService oidcService;

    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private UserNicknameService nicknameService;

    @SpyBean
    private AuthService authService;

    @SpyBean
    private JwtProvider jwtProvider;

    @SpyBean
    private UserCreateService userCreateService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("주어진 액세스 토큰이 유효하면 true를 반환한다.")
    @Test
    public void validateAccessToken(){
        //given
        String accessToken = "Bearer " + jwtProvider.getIssueToken(1L, true);

        //when
        ValidateTokenResponseDto response = authService.validateAccessToken(accessToken);

        //then
        assertThat(response.isValid()).isTrue();
    }

    @DisplayName("주어진 액세스 토큰이 유효하지 않으면 false를 반환한다.")
    @Test
    public void validateAccessToken_WithInValidToken(){
        //given
        String accessToken = "Bearer notVaild.아무값이나 넣었어.sdfhsodjf";

        //when
        ValidateTokenResponseDto response = authService.validateAccessToken(accessToken);

        //then
        assertThat(response.isValid()).isFalse();
    }

    @DisplayName("이미 존재하는 닉네임을 부여받으면 새로운 트랜잭션에서 닉네임을 재생성한다.")
    @Test
    public void getOrCreateUser_whenNicknameIsDuplicated_thenRetry() throws InterruptedException {
        //given
        SocialType socialType = SocialType.APPLE;

        String idTokenOfUser1 = "idToken1";
        String subOfUser1 = "111111111";
        when(oidcService.getOidcUserInfo(socialType, idTokenOfUser1))
                .thenReturn(OidcUserInfo.of(subOfUser1));

        String idTokenOfUser2 = "idToken2";
        String subOfUser2 = "222222222";
        when(oidcService.getOidcUserInfo(socialType, idTokenOfUser2))
                .thenReturn(OidcUserInfo.of(subOfUser2));

        when(nicknameService.generateRandomNickname())
                .thenReturn("행복한 뮤넥터1")
                .thenReturn("행복한 뮤넥터1")
                .thenReturn("깜찍한 뮤넥터3");

        //when
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1); // 시작 동기화용
        CountDownLatch completionLatch = new CountDownLatch(threadCount); // 완료 대기용

        // 첫 번째 작업
        executorService.submit(() -> {
            try {
                try {
                    startLatch.await(); // 시작 신호 대기
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                authService.getOrCreateUser(new LoginRequestDto(socialType, idTokenOfUser1));
            } finally {
                completionLatch.countDown();
            }
        });

        // 두 번째 작업
        executorService.submit(() -> {
            try {
                startLatch.await(); // 시작 신호 대기
                authService.getOrCreateUser(new LoginRequestDto(socialType, idTokenOfUser2));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                completionLatch.countDown();
            }
        });

        startLatch.countDown();
        completionLatch.await();
        executorService.shutdown();

        //then
        Optional<User> user1 = userRepository.findBySocialId(makeSocialId(socialType, subOfUser1));
        assertThat(user1).isPresent();

        Optional<User> user2 = userRepository.findBySocialId(makeSocialId(socialType, subOfUser2));
        assertThat(user2).isPresent();

        assertThat(user1.get().getNickname()).isNotEqualTo(user2.get().getNickname());

        verify(nicknameService, times(3)).generateRandomNickname();
        verify(authService, times(2)).getOrCreateUser(any());
        verify(oidcService, times(2)).getOidcUserInfo(any(), any());
    }

    @DisplayName("모든 유저는 고유한 닉네임을 가져야한다.")
    @Test
    public void getOrCreateUser_whenMultipleUsers_thenUniqueNicknames() throws InterruptedException {
        //given
        LoginRequestDto requestDto = createRequestDto();
        when(oidcService.getOidcUserInfo(requestDto.socialType(), requestDto.idToken()))
                .thenReturn(OidcUserInfo.of("sub"));
        when(userRepository.findBySocialId(anyString()))
                .thenReturn(Optional.empty());

        //when
        int numberOfThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    authService.getOrCreateUser(requestDto);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        //then
        List<User> createdUsers = userRepository.findAll();
        assertThat(createdUsers.size()).isEqualTo(numberOfThreads);

        assertThat(createdUsers.stream()
                .map(User::getNickname)
                .collect(Collectors.toSet()))
                .hasSize(numberOfThreads);
    }

    @DisplayName("JWT 발급에 문제가 생겨도 새로운 유저의 회원가입은 유지된다.")
    @Test
    public void getOrCreateUser_whenTokenIssuanceFails_thenUserIsCreated() {
        //given
        LoginRequestDto requestDto = createRequestDto();
        when(oidcService.getOidcUserInfo(requestDto.socialType(), requestDto.idToken()))
                .thenReturn(OidcUserInfo.of("sub"));
        when(jwtProvider.getIssueToken(anyLong(), anyBoolean()))
                .thenThrow(JwtException.class);

        //when //then
        assertThatThrownBy(() -> authService.getOrCreateUser(requestDto)).isInstanceOf(JwtException.class);
        assertThat(userRepository.findBySocialId(makeSocialId(SocialType.APPLE, "sub")))
                .isNotEmpty();
    }

    @DisplayName("닉네임을 재생성할 수 있는 횟수는 최대 20번이다.")
    @Test
    public void getOrCreateUser_whenNicknameGenerationExceedsMaxRetries(){
        //given
        LoginRequestDto requestDto = createRequestDto();
        when(oidcService.getOidcUserInfo(requestDto.socialType(), requestDto.idToken()))
                .thenReturn(OidcUserInfo.of("sub"));

        String duplicatedNickname = "행복한 뮤넥터1";
        userRepository.save(User.toEntity("KAKAO_sub", duplicatedNickname, Role.USER, SocialType.APPLE));
        when(nicknameService.generateRandomNickname())
                .thenReturn(duplicatedNickname);

        //when //then
        assertThatThrownBy(()-> authService.getOrCreateUser(requestDto))
                .isInstanceOf(NicknameException.class)
                .hasMessage("닉네임 생성에 실패하였습니다.");

        verify(userCreateService, times(20)).createUser(anyString(), any(SocialType.class));
    }

    private LoginRequestDto createRequestDto() {
        return new LoginRequestDto(SocialType.APPLE, "idToken");
    }

    private String makeSocialId(SocialType socialType, String sub) {
        return socialType + "_" + sub;
    }

}
package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.dto.RefreshTokenRequestDto;
import com.munecting.api.domain.user.dto.UserLoginRequestDto;
import com.munecting.api.domain.user.dto.UserTokenResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.auth.oidc.provider.OidcProviderFactory;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.GeneralException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;

    private final OidcProviderFactory oidcProviderFactory;

    private final UserRepository userRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public UserTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
        String providedToken = requestDto.refreshToken();
        Long userId = getUserIdFromToken(providedToken);
        String savedRefreshToken = getSavedUserRefreshToken(userId);

        if (savedRefreshToken == null || !savedRefreshToken.equals(providedToken)) {
            log.warn("유효하지 않은 리프레쉬 토큰입니다.");
            throw new UnauthorizedException(Status.INVALID_TOKEN);
        }

        // 새 토큰 발급
        String newAccessToken = issueNewAccessToken(userId);
        String newRefreshToken = issueNewRefreshToken(userId);

        // Redis에 새 리프레시 토큰 저장
        saveRefreshToken(userId, newRefreshToken);
        return UserTokenResponseDto.of(newAccessToken, newRefreshToken);
    }

    private Long getUserIdFromToken(String token) {
        jwtProvider.validateRefreshToken(token);
        return jwtProvider.getSubject(token);
    }

    private String getSavedUserRefreshToken(Long userId) {
        String redisKey = getRedisKey(userId);
        return redisTemplate.opsForValue().get(redisKey);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {
        String redisKey = getRedisKey(userId);
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, refreshTokenExpiration, TimeUnit.SECONDS);
    }

    private String getRedisKey(Long userId) {
        return "RT:" + userId;
    }

    @Transactional
    public UserTokenResponseDto getOrCreateUser(UserLoginRequestDto dto) {
        String email = oidcProviderFactory.getEmail(dto.socialType(), dto.idToken());
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, dto.socialType()));
        validateUser(dto.socialType(), user.getSocialType());
        return issueTokensForUser(user);
    }

    private void validateUser(SocialType providedSocial, SocialType savedSocial) {
        if (providedSocial != savedSocial) {
            throw new GeneralException("이미 가입된 이메일입니다. 이전에 사용한 로그인 수단을 사용해주세요.", Status.BAD_REQUEST);
        }
    }

    private User createUser(String email, SocialType socialType) {
        User newUser = User.builder()
                .email(email)
                .role(Role.USER)
                .socialType(socialType)
                .build();
        return userRepository.save(newUser);
    }

    private UserTokenResponseDto issueTokensForUser(User user) {
        Long userId = user.getId();

        // 토큰 발급
        String accessToken = issueNewAccessToken(userId);
        String refreshToken = issueNewRefreshToken(userId);

        // 리프레쉬 토큰 저장
        saveRefreshToken(userId, refreshToken);

        return UserTokenResponseDto.of(accessToken, refreshToken);
    }

    public String getToken(Long userId) {
        String accessToken = issueNewAccessToken(userId);
        return accessToken;
    }

    private String issueNewAccessToken(Long userId) {
        return jwtProvider.getIssueToken(userId, true);
    }

    private String issueNewRefreshToken(Long userId) {
        return jwtProvider.getIssueToken(userId, false);
    }

    public void logout(Long userId) {
        String key = getRedisKey(userId);
        String savedRefreshToken = redisTemplate.opsForValue().get(key);

        if (savedRefreshToken != null) {
            redisTemplate.delete(key);
            log.info("User {} logged out successfully", userId);
        } else {
            log.info("User {} logged out, no refresh token found", userId);
        }
    }
}

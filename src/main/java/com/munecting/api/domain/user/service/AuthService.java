package com.munecting.api.domain.user.service;

import com.munecting.api.domain.oidc.dto.OidcUserInfo;
import com.munecting.api.domain.oidc.service.OidcService;
import com.munecting.api.domain.user.dto.request.LogoutRequestDto;
import com.munecting.api.domain.user.dto.request.RefreshTokenRequestDto;
import com.munecting.api.domain.user.dto.request.LoginRequestDto;
import com.munecting.api.domain.user.dto.response.UserTokenResponseDto;
import com.munecting.api.domain.user.dto.response.ValidateTokenResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final OidcService oidcService;
    private final UserRepository userRepository;
    private final UserCreateService userCreateService;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public UserTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
        String providedToken = requestDto.refreshToken();
        Long userId = getUserIdFromRefreshToken(providedToken);
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

    private Long getUserIdFromRefreshToken(String token) {
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

    public UserTokenResponseDto getOrCreateUser(LoginRequestDto dto) {
        String socialId = validateAndObtainUserInfo(dto);
        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userCreateService.createUser(socialId, dto.socialType()));

        return issueTokensForUser(user);
    }

    private String validateAndObtainUserInfo(LoginRequestDto dto) {
        OidcUserInfo oidcUserInfo = oidcService.getOidcUserInfo(dto.socialType(), dto.idToken());

        return dto.socialType().toString() + "_" + oidcUserInfo.sub();
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
        return issueNewAccessToken(userId);
    }

    private String issueNewAccessToken(Long userId) {
        return jwtProvider.getIssueToken(userId, true);
    }

    private String issueNewRefreshToken(Long userId) {
        return jwtProvider.getIssueToken(userId, false);
    }

    @Transactional
    public void logout(LogoutRequestDto dto) {
        Long userId = getUserIdFromAccessToken(dto.accessToken());
        processLogout(userId);
    }

    private Long getUserIdFromAccessToken(String requestToken) {
        String token = extractAccessToken(requestToken);

        try {
            jwtProvider.validateTokenAtLogout(token);
        } catch (ExpiredJwtException e) {
            log.warn("tried to log out with expired access token");
        } finally {
            return jwtProvider.getSubjectByDecode(token);
        }
    }

    private void processLogout(Long userId) {
        String key = getRedisKey(userId);
        String savedRefreshToken = redisTemplate.opsForValue().get(key);

        if (savedRefreshToken != null) {
            redisTemplate.delete(key);
            log.info("User {} logged out successfully", userId);
        } else {
            log.info("User {} logged out, no refresh token found", userId);
        }
    }

    @Transactional(readOnly = true)
    public ValidateTokenResponseDto validateAccessToken(String authorizationHeaderValue) {
        try {
            String accessToken = extractAccessToken(authorizationHeaderValue);
            jwtProvider.validateAccessToken(accessToken);
            return ValidateTokenResponseDto.of(true);
        } catch (Exception e) {
            log.info("Access token validation failed: {}", e.getMessage());
            return ValidateTokenResponseDto.of(false);
        }
    }

    private String extractAccessToken(String bearerToken) {
        return jwtProvider.extractAccessToken(bearerToken);
    }

}

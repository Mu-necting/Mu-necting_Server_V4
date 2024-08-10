package com.munecting.api.global.oauth2.handler;

import com.munecting.api.global.jwt.util.JWTUtil;
import com.munecting.api.global.Redis.entity.RefreshEntity;
import com.munecting.api.global.Redis.repository.RefreshRepository;
import com.munecting.api.global.oauth2.service.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.munecting.api.global.jwt.util.JWTUtil.createCookie;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            loginSuccess(response, oAuth2User);
        } catch (Exception e) {
            throw e;
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String email = oAuth2User.getEmail();
        String role = oAuth2User.getRole().getKey();
        log.info("로그인 email =" + email);
        log.info("로그인 role =" + role);
        //토큰 생성
        String access = jwtUtil.createJwt("AccessToken", email, role, accessTokenExpirationPeriod);
        String refresh = jwtUtil.createJwt("RefreshToken", email, role, refreshTokenExpirationPeriod);
        log.info("successfulAuthentication : AccessToken :" + access);
//        addRefreshEntity(userId, refresh, 86400000L);
        RefreshEntity redis = new RefreshEntity(email, refresh, new Date(System.currentTimeMillis() + 123456789L).toString());
        refreshRepository.save(redis);
        //응답 설정
        response.setHeader("Authorization", access);
        response.addCookie(createCookie("Authorization-refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

}

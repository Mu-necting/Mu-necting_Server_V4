package com.munecting.api.global.jwt.controller;

import com.munecting.api.global.Redis.entity.RefreshEntity;
import com.munecting.api.global.Redis.repository.RefreshRepository;
import com.munecting.api.global.jwt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.munecting.api.global.jwt.util.JWTUtil.createCookie;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReissueRefreshTokenController {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue controller");
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies.length == 0) {
            return new ResponseEntity<>("Required cookie is missing or empty", HttpStatus.BAD_REQUEST);
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization-refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("Refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("Refresh token expired", HttpStatus.UNAUTHORIZED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("RefreshToken")) {

            //response status code
            return new ResponseEntity<>("no equal refresh token", HttpStatus.BAD_REQUEST);
        }
        String email = jwtUtil.getEmail(refresh);
        //DB에 저장되어 있는지 확인

        boolean isExists = refreshRepository.existsById(email);
        if (!isExists) {

            //response body
            return new ResponseEntity<>("no exist", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("AccessToken", username, role, accessTokenExpirationPeriod);
        String newRefresh = jwtUtil.createJwt("RefreshToken", username, role, refreshTokenExpirationPeriod);
        log.info("AccessToken = " + newAccess);
        log.info("RefreshToken = " + newRefresh);

        refreshRepository.deleteById(email); //안해줘도 자동 지우고 쓴다
        RefreshEntity redis = new RefreshEntity(username, newRefresh, new Date(System.currentTimeMillis() + 123456789L).toString());
        refreshRepository.save(redis);
        //response
        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("Authorization-refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

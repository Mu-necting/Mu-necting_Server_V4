package com.munecting.api.global.login.filter;

import com.munecting.api.global.Redis.repository.RefreshRepository;
import com.munecting.api.global.jwt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("logout  f filterr!!");
        //path and method verify
        String requestUri = request.getRequestURI();
//        if (!requestUri.matches("^\\/logout$")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
        if (!requestUri.matches("^\\/logout$")) {
            log.info("logout url이 아닙니다");
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        log.info("logout url입니다");
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("Authorization-refresh")) {

                refresh = cookie.getValue();
            }
        }
        log.info("refresh ="+refresh);
        //refresh null check
        if (refresh == null) {
            log.info("cookie가 비어있습니다");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("RefreshToken")) {
            log.info("refresh 토큰이 아닙니다");
            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String email = jwtUtil.getEmail(refresh);
        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsById(email);
        if (!isExist) {
            log.info("존재하지않습니다");
            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteById(email);

        //Refresh 토큰 Cookie 값 0 쿠키 초기화
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
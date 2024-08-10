package com.munecting.api.global.jwt.filter;

import com.munecting.api.global.jwt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");
        log.info("JWTFilter : accessToken : "+accessToken);
        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }
        // 토큰이 "Bearer "로 시작하지 않으면 다음 필터로 넘김
        if (!accessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);

            return;
        }
        String pureAccessToken = accessToken.replace("Bearer ", "");

        log.info("pureAccessToken =" + pureAccessToken);
        log.info("accessToken =" + accessToken);
        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(pureAccessToken);

        if (!category.equals("AccessToken")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        /**
         * 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
         *  JWTFilter에서 Access 토큰의 만료로 인한 특정한 상태 코드가 응답되면
         *  프론트측 Axios Interceptor와 같은 예외 핸들러에서
         *  Access 토큰 재발급을 위한 Refresh을 서버측으로 전송한다.
         */
        try {
            jwtUtil.isExpired(pureAccessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰에서 email과 role 획득
        String email = jwtUtil.getEmail(pureAccessToken);
        String role = jwtUtil.getRole(pureAccessToken);

        log.info("email =" + email);
        log.info("role =" + role);

        String role1 = role.replace("ROLE_", "");
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(email)
                .password("")
                .roles(role1)
                .build();


        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetailsUser, null, userDetailsUser.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

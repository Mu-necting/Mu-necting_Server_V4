package com.munecting.api.global.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.global.Redis.entity.RefreshEntity;
import com.munecting.api.global.Redis.repository.RefreshRepository;
import com.munecting.api.global.jwt.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static com.munecting.api.global.jwt.util.JWTUtil.createCookie;

@Slf4j
public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
//    private JWTUtil jwtUtil;
//    private RefreshRepository refreshRepository;
    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";
    private static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리
    private static final AntPathRequestMatcher CUSTOM_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/login/auth", "POST");
    private final ObjectMapper objectMapper;



    public CustomLoginFilter( ObjectMapper objectMapper) {
        super(CUSTOM_ANT_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;


    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }
        // JSON타입만 로그인
        /**
         * 클라이언트 요청에서 email, password 추출 ->  email과 password 추출 안될 시 에라상황 산정
         * 스프링 시큐리티에서 email password를 검증하기 위해서 token에 담기
         * UsernamePasswordAuthenticationToken의 파라미터 principal, credentials에 대입
         */
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        String email = usernamePasswordMap.get(EMAIL_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);
        log.info("email= "+email);
        log.info("password ="+password);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

}

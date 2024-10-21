package com.munecting.api.global.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.global.auth.filter.ExceptionHandlerFilter;
import com.munecting.api.global.auth.filter.JwtAuthenticationFilter;
import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.error.exception.ForbiddenException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import com.munecting.api.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final ResponseUtil responseUtil;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    private static final String[] ALLOWED_URL = {
            "/api/auth/**",
            "/error/**",
            "/reissue",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/css/**","/images/**","/js/**","/favicon.ico",
            "/musics/**",
            "/address/**",
            "/actuator/health"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf disable
                .csrf(AbstractHttpConfigurer::disable)

                // From 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP Basic 인증 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALLOWED_URL).permitAll()
                        .anyRequest().authenticated())

                // filter
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint((request, response, authException) ->
                                        handleAuthenticationEntryPoint(response)
                                )
                                .accessDeniedHandler((request, response, accessDeniedException) ->
                                        handleAccessDenied(response)
                                )
                )
        ;

        return http.build();
    }

    private void handleAuthenticationEntryPoint(HttpServletResponse response) throws IOException {
        log.warn("인증되지 않은 사용자의 접근입니다.");
        responseUtil.sendException(response, new UnauthorizedException());
    }

    private void handleAccessDenied(HttpServletResponse response) throws IOException {
        log.warn("권한이 없는 사용자의 접근입니다.");
        responseUtil.sendException(response, new ForbiddenException());
    }
}

package com.munecting.api.global.auth.filter;

import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.util.AllowedPathPatternProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final String authHeader;
    private final AllowedPathPatternProvider allowedPathPatternProvider;

    public JwtAuthenticationFilter(
            JwtProvider jwtProvider,
            @Value("${spring.security.auth.header}") String authHeader,
            AllowedPathPatternProvider allowedPathPatternProvider
    ) {
        this.jwtProvider = jwtProvider;
        this.authHeader = authHeader;
        this.allowedPathPatternProvider = allowedPathPatternProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isPathWhitelisted(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        processAuthentication(request);
        filterChain.doFilter(request, response);
    }

    private boolean isPathWhitelisted(HttpServletRequest request) {
        return allowedPathPatternProvider.isPathWhitelisted(request.getRequestURI());
    }

    private void processAuthentication(HttpServletRequest request) {
        Optional<String> bearerToken = Optional.ofNullable(request.getHeader(authHeader));
        bearerToken.ifPresent(it -> {
            String accessToken = jwtProvider.extractAccessToken(it);
            jwtProvider.validateAccessToken(accessToken);
            setAuthentication(accessToken);
        });
    }

    private void setAuthentication(String accessToken) {
        UsernamePasswordAuthenticationToken authentication = jwtProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

package com.munecting.api.global.auth.filter;

import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import com.munecting.api.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ResponseUtil responseUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // JwtAuthenticationFilter 실행
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException | EntityNotFoundException e ) {
            responseUtil.sendException(response,e);
        } catch (Exception ee) {
            responseUtil.sendError(response);
        }
    }
}

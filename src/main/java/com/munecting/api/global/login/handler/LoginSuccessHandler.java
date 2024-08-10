package com.munecting.api.global.login.handler;

import com.munecting.api.global.Redis.entity.RefreshEntity;
import com.munecting.api.global.Redis.repository.RefreshRepository;
import com.munecting.api.global.jwt.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static com.munecting.api.global.jwt.util.JWTUtil.createCookie;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("successfulAuthentication!!");
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        if (userDetails == null) {
            log.error("userDetails is null");
        }
        String email = userDetails.getUsername();
        log.info("email =" + email);
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication1 = " + authentication1);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        log.info("auth = " + auth);
        String role = auth.getAuthority();
        log.info("role = " + role);


        //crate jwt
        String access = jwtUtil.createJwt("AccessToken", email, role, 3600000L);
        String refresh = jwtUtil.createJwt("RefreshToken", email, role, 604800000L);
        log.info("successfulAuthentication : AccessToken :" + access);
        RefreshEntity redis = new RefreshEntity(email, refresh, new Date(System.currentTimeMillis() + 604800000L).toString());
        refreshRepository.save(redis);


        //응답 설정
        response.setHeader("Authorization", access);
        response.addCookie(createCookie("Authorization-refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }
}

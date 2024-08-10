package com.munecting.api.global.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.global.login.filter.CustomLoginFilter;
import com.munecting.api.global.login.filter.CustomLogoutFilter;
import com.munecting.api.global.jwt.filter.JWTFilter;
import com.munecting.api.global.jwt.util.JWTUtil;
import com.munecting.api.global.login.handler.LoginFailureHandler;
import com.munecting.api.global.login.handler.LoginSuccessHandler;
import com.munecting.api.global.login.service.CustomUserDetailService;
import com.munecting.api.global.oauth2.service.CustomOAuth2UserService;
import com.munecting.api.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.munecting.api.global.oauth2.handler.OAuth2LoginSuccessHandler;
import com.munecting.api.global.Redis.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshRepository refreshRepository;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailService customUserDetailService;
    private static final String[] ALLOWED_URL = {
            "/",
            "/login",
            "/login/auth",
            "/login/register",
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
            "/css/**","/images/**","/js/**","/favicon.ico"

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(
                        cors -> cors.configurationSource(
                                corsConfigurationSource()
                        )
                );
        //csrf disable
        http
                .csrf((auth) -> auth.disable());
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());
        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable)

                );
        //== URL별 권한 관리 옵션 ==//
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(ALLOWED_URL).permitAll()
                        .anyRequest().authenticated());
        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler));
        //== Filter ==//

        http
                .addFilterBefore(customLogoutFilter(), LogoutFilter.class);

        http
                .addFilterAfter(customLoginFilter(),LogoutFilter.class);
        http
                .addFilterBefore(jwtFilter(), CustomLoginFilter.class);


        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(customPasswordEncoder());
        provider.setUserDetailsService(customUserDetailService);
        return new ProviderManager(provider);
    }
    @Bean
    public CustomLoginFilter customLoginFilter() {
        CustomLoginFilter customJsonUsernamePasswordLoginFilter
                = new CustomLoginFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtUtil, refreshRepository);
    }
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtil);
    }
    @Bean
    public CustomLogoutFilter customLogoutFilter() {
        return new CustomLogoutFilter(jwtUtil,refreshRepository);
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
        @Bean
        public PasswordEncoder customPasswordEncoder() {
            return new BCryptPasswordEncoder();

        }
}

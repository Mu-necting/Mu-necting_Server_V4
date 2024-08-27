package com.munecting.api.global.config;

import com.munecting.api.global.auth.interceptor.UserAuthorizationInterceptor;
import com.munecting.api.global.auth.resolver.UserIdArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserIdArgumentResolver userIdArgumentResolver;
    private final UserAuthorizationInterceptor userAuthorizationInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthorizationInterceptor)
                .addPathPatterns("/api/users/{userId}");
    }
}

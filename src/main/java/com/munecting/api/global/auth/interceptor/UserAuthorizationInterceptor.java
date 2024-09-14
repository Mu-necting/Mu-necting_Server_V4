package com.munecting.api.global.auth.interceptor;

import com.munecting.api.global.auth.user.UserPrincipalDetails;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.ForbiddenException;
import com.munecting.api.global.error.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@Slf4j
public class UserAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // HTTP DELETE METHOD 요청만 handle
        if (!"DELETE".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String userIdFromPath = extractUserIdFromPath(handler);
        if (userIdFromPath == null) {
            log.warn("user ID from path is null");
            throw new GeneralException(Status.BAD_REQUEST);
        }

        Long requestedUserId = parseUserId(userIdFromPath);
        if (requestedUserId == getUserFromToken().getId()) {
            return true;
        }

        log.info("삭제 요청 id : {}, 액세스 토큰으로부터 추출한 id: {}", userIdFromPath, getUserFromToken().getId());
        throw new ForbiddenException();
    }

    private UserPrincipalDetails getUserFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userDetails = (UserPrincipalDetails) auth.getPrincipal();
        return userDetails;
    }

    private String extractUserIdFromPath(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            PathVariable pv = handlerMethod.getMethodParameters()[0].getParameterAnnotation(PathVariable.class);

            if (pv != null) {
                String userId = ServletUriComponentsBuilder.fromCurrentRequest().build().getPathSegments().get(2);
                return userId;
            }
        }
        return null;
    }

    private Long parseUserId(String userIdFromPath) {
        try {
            return Long.valueOf(userIdFromPath);
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", userIdFromPath, e);
            throw new GeneralException(Status.BAD_REQUEST);
        }
    }
}

package com.munecting.api.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.error.exception.ForbiddenException;
import com.munecting.api.global.error.exception.InvalidValueException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.munecting.api.global.common.dto.response.Status.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class ResponseUtil {

    private final ObjectMapper objectMapper;

    public void sendException(HttpServletResponse response, Exception e) throws IOException {
        setResponseDefaults(response);
        ApiResponse<Object> failure = null;

        if (e instanceof UnauthorizedException ue) {
            response.setStatus(ue.getStatus().getHttpStatus().value());
            failure = ApiResponse.onFailure(ue.getStatus().getCode(), ue.getMessage(), null);
        } else if (e instanceof InvalidValueException ie) {
            response.setStatus(ie.getStatus().getHttpStatus().value());
            failure = ApiResponse.onFailure(ie.getStatus().getCode(), ie.getMessage(), null);
        } else if (e instanceof EntityNotFoundException ee) {
            response.setStatus(ee.getStatus().getHttpStatus().value());
            failure = ApiResponse.onFailure(ee.getStatus().getCode(), ee.getMessage(), null);
        } else if (e instanceof ForbiddenException fe) {
            response.setStatus(fe.getStatus().getHttpStatus().value());
            failure = ApiResponse.onFailure(fe.getStatus().getCode(), fe.getMessage(), null);
        }
        response.getWriter().write(objectMapper.writeValueAsString(failure));
    }

    public void sendError(HttpServletResponse response) throws IOException {
        setResponseDefaults(response);
        response.setStatus(INTERNAL_SERVER_ERROR.getHttpStatus().value());
        ApiResponse<Object> failue = ApiResponse.onFailure(
                INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMessage(), null);
        response.getWriter().write(objectMapper.writeValueAsString(failue));
    }

    private void setResponseDefaults(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }
}

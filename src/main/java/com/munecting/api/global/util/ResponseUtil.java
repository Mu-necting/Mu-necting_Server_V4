package com.munecting.api.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.error.exception.InvalidValueException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.munecting.api.global.common.dto.response.Status.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class ResponseUtil {

    private final ObjectMapper objectMapper;

    public void sendException(HttpServletResponse response, Exception e) throws IOException {
        setResponseDefaults(response);

        if (e instanceof UnauthorizedException ue) {
            response.setStatus(ue.getStatus().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ue.getBody()));
        } else if (e instanceof InvalidValueException ie) {
            response.setStatus(ie.getStatus().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ie.getBody()));
        } else if (e instanceof EntityNotFoundException ee) {
            response.setStatus(ee.getStatus().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ee.getBody()));
        }
    }

    public void sendError(HttpServletResponse response) throws IOException {
        setResponseDefaults(response);
        response.setStatus(INTERNAL_SERVER_ERROR.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(INTERNAL_SERVER_ERROR.getBody()));
    }

    private void setResponseDefaults(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }
}

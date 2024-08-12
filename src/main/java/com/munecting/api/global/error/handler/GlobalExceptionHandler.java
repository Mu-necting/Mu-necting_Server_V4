package com.munecting.api.global.error.handler;

import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Body;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(annotations = {RestController.class})
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Body> handleGeneralException(
            GeneralException e
    ) {
        log.warn(">>> handle: GeneralException | " + e.getStatus() + " " + e.getMessage());
        return new ResponseEntity<>(e.getBody(), e.getStatus().getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Body> unexpectedException(
            Exception unexpectedException
    ) {
        log.error("예상치 못한 오류 발생: {}", unexpectedException.getMessage());
        log.error("발생 지점: {}", unexpectedException.getStackTrace()[0]);
        return new ResponseEntity<>(Status.INTERNAL_SERVER_ERROR.getBody(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}



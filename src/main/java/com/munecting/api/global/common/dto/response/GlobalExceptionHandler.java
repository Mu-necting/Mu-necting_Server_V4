package com.munecting.api.global.common.dto.response;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> unexpectedException(
            Exception unexpectedException
    ) {
        log.error("예상치 못한 오류 발생: {}", unexpectedException.getMessage());
        log.error("발생 지점: {}", unexpectedException.getStackTrace()[0]);
        Body body = Status.INTERNAL_SERVER_ERROR.getBody();
        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> exception(
            GeneralException generalException
    ) {
        Body body = generalException.getBody();
        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), null);
        log.warn(response.getCode() + " " + response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}



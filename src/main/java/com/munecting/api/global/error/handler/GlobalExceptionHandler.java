package com.munecting.api.global.error.handler;

import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Body;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


@RestControllerAdvice(annotations = {RestController.class})
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {


    /**
     * Valid & Validated annotation의 binding error를 handling합니다.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.error(">>> handle: MethodArgumentNotValidException ", e);
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
                });

        ApiResponse<Map<String, String>> response = ApiResponse.onFailure(Status.BAD_REQUEST.getCode(), Status.BAD_REQUEST.getMessage(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * GeneralException을 handling합니다.
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(
            GeneralException e
    ) {
        log.warn(">>> handle: GeneralException | " + e.getStatus() + " :" + e.getMessage());
        ApiResponse<Object> response = ApiResponse.onFailure(e.getStatus().getCode(), e.getMessage(), null);
        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(response);
    }

    /**
     * 그 외의 모든 예외를 handling 합니다.
     */
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

}



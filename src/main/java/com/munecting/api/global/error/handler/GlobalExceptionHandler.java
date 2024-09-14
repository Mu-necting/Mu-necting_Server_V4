package com.munecting.api.global.error.handler;

import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Body;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Valid & Validated annotation의 binding error를 handling 합니다.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.error(">>> handle: MethodArgumentNotValidException", e);

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
     * 필수 쿼리 파라미터를 누락한 경우 발생하는 error를 handling 합니다.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        log.warn(">>> handle: MissingServletRequestParameterException", e);

        ApiResponse<Object> response = ApiResponse.onFailure(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 지원하지 않는 HTTP method로 요청 시 발생하는 error를 handling합니다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        log.warn(">>> handle: HttpRequestMethodNotSupportedException", e);

        ApiResponse<Object> response = ApiResponse.onFailure(HttpStatus.METHOD_NOT_ALLOWED.toString(), e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 존재하지 않는 HTTP URI 요청 시 발생하는 error를 handling합니다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceException(
            NoResourceFoundException e
    ) {
        log.warn(">>> handle: NoResourceException", e);

        ApiResponse<Object> response = ApiResponse.onFailure(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * GeneralException을 handling 합니다.
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(
            GeneralException e
    ) {
        log.warn(">>> handle: GeneralException | " +e.getStatus() + e);

        ApiResponse<Object> response = ApiResponse.onFailure(e.getStatus().getCode(), e.getMessage(), null);
        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(response);
    }

    /**
     * 그 외의 모든 예외를 handling 합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> unexpectedException(
            Exception e
    ) {
        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
        log.error("발생 지점: {}", e.getStackTrace()[0]);

        Body body = Status.INTERNAL_SERVER_ERROR.getBody();
        ApiResponse<Object> response = ApiResponse.onFailure(body.getCode(), body.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}



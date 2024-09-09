package com.munecting.api.global.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.onSuccess(Status.CREATED.getCode(), Status.CREATED.getMessage(), data);
    }

    public static <T> ApiResponse<T> onSuccess(String status, String message, T data) {
        return new ApiResponse<>(true, status, message, data);
    }

    public static <T> ApiResponse<T> onFailure(String status, String message, T data) {
        return  new ApiResponse<>(false, status, message, data);
    }
}

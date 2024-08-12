package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Body;
import com.munecting.api.global.common.dto.response.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GeneralException extends RuntimeException {
    private Status status;

    public Body getBody() {
        return this.status.getBody();
    }
}
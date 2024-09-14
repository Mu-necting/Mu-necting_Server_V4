package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Body;
import com.munecting.api.global.common.dto.response.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private Status status;

    public GeneralException(String message, Status errorStatus) {
        super(message);
        this.status = errorStatus;
    }

    public GeneralException(Status errorStatus) {
        super(errorStatus.getMessage());
        this.status = errorStatus;
    }

    public Body getBody() {
        return this.status.getBody();
    }
}

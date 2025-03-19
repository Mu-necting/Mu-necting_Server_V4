package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

public class ConflictException extends GeneralException {

    public ConflictException() {
        super(Status.CONFLICT);
    }

    public ConflictException(Status errorStatus) {
        super(errorStatus);
    }

    public ConflictException(Status errorStatus, String message) {
        super(message, errorStatus);
    }

}

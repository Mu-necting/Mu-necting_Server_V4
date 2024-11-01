package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

import static com.munecting.api.global.common.dto.response.Status.BAD_REQUEST;

public class InvalidValueException extends GeneralException {

    public InvalidValueException() {
        super(BAD_REQUEST);
    }

    public InvalidValueException(Status errorStatus) {
        super(errorStatus);
    }

    public InvalidValueException(Status errorStatus, String message) {
        super(message, errorStatus);
    }
}

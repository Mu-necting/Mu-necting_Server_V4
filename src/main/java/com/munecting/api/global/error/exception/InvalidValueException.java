package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

public class InvalidValueException extends GeneralException {

    public InvalidValueException() {
        super(Status.BAD_REQUEST);
    }

    public InvalidValueException(Status errorStatus) {
        super(errorStatus);
    }
}

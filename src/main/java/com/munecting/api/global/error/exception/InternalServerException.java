package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

import static com.munecting.api.global.common.dto.response.Status.INTERNAL_SERVER_ERROR;

public class InternalServerException extends GeneralException{

    public InternalServerException() {
        super(INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String message) {
        super(message, INTERNAL_SERVER_ERROR);
    }

}

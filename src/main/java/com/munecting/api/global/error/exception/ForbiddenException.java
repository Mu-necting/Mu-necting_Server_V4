package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

public class ForbiddenException extends GeneralException{
    public ForbiddenException() {
        super(Status.FORBIDDEN);
    }

    public ForbiddenException(Status errorStatus) {
        super(errorStatus);
    }
}

package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

public class UnauthorizedException extends GeneralException {
    public UnauthorizedException() {
        super(Status.UNAUTHORIZED);
    }

    public UnauthorizedException(Status errorStatus) {
        super(errorStatus);
    }
}

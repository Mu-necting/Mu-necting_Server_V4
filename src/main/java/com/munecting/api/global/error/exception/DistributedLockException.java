package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

import static com.munecting.api.global.common.dto.response.Status.BAD_REQUEST;

public class DistributedLockException extends GeneralException{

    public DistributedLockException() {
        super(BAD_REQUEST);
    }

    public DistributedLockException(Status errorStatus) {
        super(errorStatus);
    }
}

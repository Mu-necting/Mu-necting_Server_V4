package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

import static com.munecting.api.global.common.dto.response.Status.INVALID_ID_TOKEN;

public class OidcException extends GeneralException{

    public OidcException() {
        super(INVALID_ID_TOKEN);
    }

    public OidcException(Status errorStatus) {
        super(errorStatus);
    }

    public OidcException(String message) {
        super(message, INVALID_ID_TOKEN);
    }
}

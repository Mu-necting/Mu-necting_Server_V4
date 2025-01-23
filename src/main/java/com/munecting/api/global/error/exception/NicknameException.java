package com.munecting.api.global.error.exception;

import com.munecting.api.global.common.dto.response.Status;

import static com.munecting.api.global.common.dto.response.Status.NICKNAME_GENERATION_FAILED;

public class NicknameException extends GeneralException{

    public NicknameException() {
        super(NICKNAME_GENERATION_FAILED);
    }

}

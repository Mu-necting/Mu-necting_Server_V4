package com.munecting.api.global.common.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private Status status;

    public Body getBody() {
        return this.status.getBody();
    }

}

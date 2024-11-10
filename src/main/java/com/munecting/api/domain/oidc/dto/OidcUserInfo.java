package com.munecting.api.domain.oidc.dto;

import lombok.Builder;

@Builder
public record OidcUserInfo(
        String sub
) {

    public static OidcUserInfo of(String sub) {
        return OidcUserInfo.builder()
                .sub(sub)
                .build();
    }
}

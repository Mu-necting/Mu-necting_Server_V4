package com.munecting.api.domain.oidc.dto;

import lombok.Builder;

@Builder
public record OidcUserInfo(
        String sub,
        String email
) {

    public static OidcUserInfo of(String sub, String email) {
        return OidcUserInfo.builder()
                .sub(sub)
                .email(email)
                .build();
    }
}

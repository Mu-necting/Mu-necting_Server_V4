package com.munecting.api.domain.oidc.strategy;

import com.munecting.api.domain.oidc.dto.OidcUserInfo;

public interface OidcStrategy {
    OidcUserInfo authenticate(String idToken);
}

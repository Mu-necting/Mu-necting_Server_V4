package com.munecting.api.domain.oidc.dto;

import com.munecting.api.domain.oidc.dto.OidcPublicKey;
import com.munecting.api.global.error.exception.InternalServerException;

import java.util.List;

public record OidcPublicKeyList(
        List<OidcPublicKey> keys
) {

    public OidcPublicKey getMatchedKey(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(() -> new InternalServerException());
    }
}


package com.munecting.api.global.auth.oidc.publicKey;

import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKey;
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


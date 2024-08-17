package com.munecting.api.global.auth.oidc.publicKey;

public record OidcPublicKey(
        String kid,
        String kty,
        String alg,
        String use,
        String n,
        String e
) {
}

package com.munecting.api.global.auth.oidc.publicKey.client;

import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKeyList;

public interface PublicKeyClient {
    OidcPublicKeyList getPublicKeys();
}

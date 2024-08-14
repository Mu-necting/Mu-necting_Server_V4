package com.munecting.api.global.auth.oidc.provider;

public interface OidcProvider {

    String getEmail(String idToken);

}

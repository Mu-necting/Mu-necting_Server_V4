package com.munecting.api.global.auth.oidc.publicKey.client;

import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKeyList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApplePublicKeyClient implements PublicKeyClient {

    private final RestClient restClient;
    private final String publicKeyUrl;

    public ApplePublicKeyClient(
            RestClient restClient,
            @Value("${spring.security.oauth.apple.public-key-url}") String publicKeyUrl
    ) {
        this.restClient = restClient;
        this.publicKeyUrl = publicKeyUrl;
    }

    public OidcPublicKeyList getPublicKeys() {
        return restClient.get()
                .uri(publicKeyUrl)
                .retrieve()
                .body(OidcPublicKeyList.class);
    }
}

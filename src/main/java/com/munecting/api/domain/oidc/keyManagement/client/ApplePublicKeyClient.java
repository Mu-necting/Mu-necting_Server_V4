package com.munecting.api.domain.oidc.keyManagement.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApplePublicKeyClient extends PublicKeyClient {

    public ApplePublicKeyClient(
            RestClient restClient,
            @Value("${spring.security.oauth.apple.public-key-url}") String publicKeyUrl
    ) {
        super(restClient,publicKeyUrl);
    }
}

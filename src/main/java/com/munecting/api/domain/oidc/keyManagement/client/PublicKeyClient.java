package com.munecting.api.domain.oidc.keyManagement.client;

import com.munecting.api.domain.oidc.dto.OidcPublicKeyList;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public abstract class PublicKeyClient {

    protected final RestClient restClient;
    protected final String publicKeyUrl;

    public OidcPublicKeyList getPublicKeys() {
        return restClient.get()
                .uri(publicKeyUrl)
                .retrieve()
                .body(OidcPublicKeyList.class);
    }
}

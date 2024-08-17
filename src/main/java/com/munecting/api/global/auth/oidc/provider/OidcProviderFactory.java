package com.munecting.api.global.auth.oidc.provider;

import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.global.error.exception.InternalServerException;
import com.munecting.api.global.error.exception.OidcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OidcProviderFactory {

    private final Map<SocialType, OidcProvider> oidcProviderByType;

    public OidcProviderFactory(List<OidcProvider> providers) {
        oidcProviderByType = new EnumMap<>(SocialType.class);
        providers.stream()
                .forEach(provider -> {
                    String socialTypeName = provider.getClass().getSimpleName().replace("AuthProvider", "").toUpperCase();
                    SocialType socialType = SocialType.valueOf(socialTypeName);
                    oidcProviderByType.put(socialType, provider);
                });
    }

    public String getEmail(SocialType socialType, String idToken) {
        OidcProvider oidcProvider = oidcProviderByType.get(socialType);

        try {
            return oidcProvider.getEmail(idToken);
        }
        catch (Exception e) {
            throw new OidcException(e.getMessage());
        }
    }
}

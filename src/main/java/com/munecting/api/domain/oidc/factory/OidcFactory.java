package com.munecting.api.domain.oidc.factory;

import com.munecting.api.domain.oidc.strategy.OidcStrategy;
import com.munecting.api.domain.user.constant.SocialType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OidcFactory {

    private final Map<SocialType, OidcStrategy> oidcStrategyBySocialType;

    public OidcFactory(List<OidcStrategy> strategies) {
        oidcStrategyBySocialType = new EnumMap<>(SocialType.class);

        strategies.stream().forEach(oidcStrategy -> {
            String socialTypeName = oidcStrategy.getClass().getSimpleName().replace("OidcStrategy", "").toUpperCase();
            SocialType socialType = SocialType.valueOf(socialTypeName);
            oidcStrategyBySocialType.put(socialType, oidcStrategy);
        });
    }

    public OidcStrategy getOidcStrategy(SocialType socialType) {
        return oidcStrategyBySocialType.get(socialType);
    }
}

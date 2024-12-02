package com.munecting.api.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPatternParser;

@Component
public class AllowedPathPatternProvider {

    private final String[] whitelistPatterns;

    public AllowedPathPatternProvider(
            @Value("${spring.security.whitelist.patterns}")
            String[] whitelistPatterns
    ) {
        this.whitelistPatterns = whitelistPatterns;
    }

    public String[] getWhitelistPatterns() {
        return whitelistPatterns;
    }

}

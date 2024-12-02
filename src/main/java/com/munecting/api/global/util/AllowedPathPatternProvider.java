package com.munecting.api.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;

@Component
public class AllowedPathPatternProvider {

    private final PathPatternParser patternParser;
    private final String[] whitelistPatterns;

    public AllowedPathPatternProvider(
            PathPatternParser patternParser,
            @Value("${spring.security.whitelist.patterns}")
            String[] whitelistPatterns
    ) {
        this.patternParser = patternParser;
        this.whitelistPatterns = whitelistPatterns;
    }

    public boolean isPathWhitelisted(final String path) {
        PathContainer requestPath = parsePathContainer(path);

        return Arrays.stream(whitelistPatterns)
                .map(this::parsePathPattern)
                .anyMatch(whitePathPattern -> whitePathPattern.matches(requestPath));
    }

    private PathPattern parsePathPattern(String whitePattern) {
        return patternParser.parse(whitePattern);
    }

    private PathContainer parsePathContainer(String path) {
        return PathContainer.parsePath(path);
    }

    public String[] getWhitelistPatterns() {
        return whitelistPatterns;
    }

}

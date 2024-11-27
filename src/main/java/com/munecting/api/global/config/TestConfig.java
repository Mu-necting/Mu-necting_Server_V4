package com.munecting.api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@Profile("test")
@PropertySource(value = {"classpath:env-common.properties", "classpath:env-test.properties"})
public class TestConfig {
}

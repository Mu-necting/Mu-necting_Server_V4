package com.munecting.api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")
@PropertySource(value = {"classpath:env-common.properties", "classpath:env-dev.properties"})
public class DevConfig {
}

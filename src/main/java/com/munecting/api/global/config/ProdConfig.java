package com.munecting.api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@Profile("prod")
@PropertySource(value = {"classpath:env-common.properties","classpath:env-prod.properties"})
public class ProdConfig {
}

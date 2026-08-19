package com.confiapix.infrastructure.integration.stone.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(StoneProperties.class)
public class StoneIntegrationConfig {

    static {
        // Stone Online exige Host virtual (sdx-ecommerce-payments / sdx-payments).
        // O HttpClient do JDK bloqueia override de Host sem esta propriedade.
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "host");
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}

package com.confiapix.infrastructure.config;

import com.confiapix.domain.service.ReconciliationEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ReconciliationEngine reconciliationEngine() {
        return new ReconciliationEngine();
    }
}

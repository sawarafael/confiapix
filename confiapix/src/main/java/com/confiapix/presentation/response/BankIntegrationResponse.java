package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class BankIntegrationResponse {

    private UUID id;
    private UUID tenantId;
    private String provider;
    private String clientId;
    private String accountRef;
    private String merchantRef;
    private Map<String, String> config;
    private boolean active;
    private Instant updatedAt;
}

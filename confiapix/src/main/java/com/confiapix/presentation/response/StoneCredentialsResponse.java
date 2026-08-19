package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class StoneCredentialsResponse {

    private UUID id;
    private UUID tenantId;
    private String authMode;
    private String businessModel;
    private String clientId;
    private String accountId;
    private String merchantId;
    private boolean active;
    private Instant updatedAt;
}

package com.confiapix.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class TenantAccessResponse {

    private UUID id;
    private String name;
    private String plan;
    private boolean active;
    private String adminEmail;
    private String adminName;
    private Instant createdAt;
}

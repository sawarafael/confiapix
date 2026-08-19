package com.confiapix.infrastructure.tenant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TenantContext {

    private final UUID tenantId;
    private final UUID userId;
    private final String email;
}

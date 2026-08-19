package com.confiapix.application.usecase.bank;

import com.confiapix.application.port.bank.BankProviderRegistry;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.BankConnectionTestResponse;
import com.confiapix.presentation.response.BankSyncResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankIntegrationOperationsUseCase {

    private final BankProviderRegistry providerRegistry;
    private final StoneProperties stoneProperties;

    @Transactional(readOnly = true)
    public BankConnectionTestResponse testConnection(String provider) {
        return providerRegistry.requireForProvider(provider).testConnection(TenantContextHolder.getTenantId());
    }

    @Transactional
    public BankSyncResponse sync(String provider) {
        return providerRegistry.requireForProvider(provider)
                .syncRecent(TenantContextHolder.getTenantId(), stoneProperties.getDefaultPixPageLimit());
    }

    @Transactional
    public BankSyncResponse syncForTenant(String provider, UUID tenantId, int limit) {
        return providerRegistry.requireForProvider(provider).syncRecent(tenantId, limit);
    }
}

package com.confiapix.infrastructure.integration.bank.job;

import com.confiapix.application.usecase.bank.BankIntegrationOperationsUseCase;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.presentation.response.BankSyncResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "confiapix.stone.sync-job-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class BankPixSynchronizationJob {

    private final BankIntegrationJpaRepository integrationRepository;
    private final BankIntegrationOperationsUseCase operationsUseCase;
    private final StoneProperties stoneProperties;

    @Scheduled(fixedDelayString = "${confiapix.stone.pix-sync-interval-ms:300000}")
    public void synchronizePix() {
        for (BankIntegrationJpaEntity integration : integrationRepository.findByActiveTrue()) {
            if (!supportsSync(integration.getProvider())) {
                continue;
            }
            try {
                BankSyncResponse result = operationsUseCase.syncForTenant(
                        integration.getProvider(),
                        integration.getTenantId(),
                        stoneProperties.getDefaultPixPageLimit());
                if (result.getImported() > 0) {
                    log.info("Sync {} tenant={} fetched={} imported={} reconciled={}",
                            integration.getProvider(),
                            integration.getTenantId(),
                            result.getFetched(),
                            result.getImported(),
                            result.getReconciled());
                }
            } catch (Exception ex) {
                log.error("Falha sync {} tenant={}: {}",
                        integration.getProvider(), integration.getTenantId(), ex.getMessage());
            }
        }
    }

    private boolean supportsSync(String provider) {
        return BankProviderCodes.STONE.equals(BankProviderCodes.normalize(provider));
    }
}

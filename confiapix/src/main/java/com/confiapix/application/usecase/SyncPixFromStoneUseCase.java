package com.confiapix.application.usecase;

import com.confiapix.application.port.StonePixPort;
import com.confiapix.application.service.NotificationService;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.StoneSyncResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncPixFromStoneUseCase {

    private final StonePixPort stonePixPort;
    private final ImportPixUseCase importPixUseCase;
    private final NotificationService notificationService;
    private final StoneProperties stoneProperties;

    @Transactional
    public StoneSyncResponse syncRecent() {
        return syncForTenant(TenantContextHolder.getTenantId(), stoneProperties.getDefaultPixPageLimit());
    }

    @Transactional
    public StoneSyncResponse syncRecent(int limit) {
        return syncForTenant(TenantContextHolder.getTenantId(), limit);
    }

    @Transactional
    public StoneSyncResponse syncForTenant(UUID tenantId, int limit) {
        List<PixTransaction> fetched = stonePixPort.findRecentPix(tenantId, limit);

        int imported = 0;
        int reconciled = 0;

        for (PixTransaction candidate : fetched) {
            ImportPixUseCase.ImportPixResult result = importPixUseCase.importAndReconcile(tenantId, candidate);
            if (result.imported()) {
                imported++;
            }
            if (result.reconciled()) {
                reconciled++;
            }
        }

        notificationService.notifyStoneSync(tenantId, imported);

        return StoneSyncResponse.builder()
                .fetched(fetched.size())
                .imported(imported)
                .reconciled(reconciled)
                .build();
    }
}

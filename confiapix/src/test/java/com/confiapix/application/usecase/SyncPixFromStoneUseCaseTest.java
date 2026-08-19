package com.confiapix.application.usecase;

import com.confiapix.application.port.StonePixPort;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.domain.valueobject.ReconciliationStatus;
import com.confiapix.domain.valueobject.Txid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncPixFromStoneUseCaseTest {

    @Mock
    private StonePixPort stonePixPort;

    @Mock
    private ImportPixUseCase importPixUseCase;

    @Mock
    private com.confiapix.application.service.NotificationService notificationService;

    @InjectMocks
    private SyncPixFromStoneUseCase syncPixFromStoneUseCase;

    @Test
    void shouldSyncAndImportPixForTenant() {
        UUID tenantId = UUID.randomUUID();
        PixTransaction candidate = buildPix(tenantId, "TX001");
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setStatus(ReconciliationStatus.MATCHED);

        when(stonePixPort.findRecentPix(tenantId, 50)).thenReturn(List.of(candidate));
        when(importPixUseCase.importAndReconcile(eq(tenantId), any(PixTransaction.class)))
                .thenReturn(ImportPixUseCase.ImportPixResult.imported(Optional.of(reconciliation)));

        var result = syncPixFromStoneUseCase.syncForTenant(tenantId, 50);

        assertThat(result.getFetched()).isEqualTo(1);
        assertThat(result.getImported()).isEqualTo(1);
        assertThat(result.getReconciled()).isEqualTo(1);
    }

    private PixTransaction buildPix(UUID tenantId, String txid) {
        PixTransaction pix = new PixTransaction();
        pix.setTenantId(tenantId);
        pix.setTxid(Txid.of(txid));
        pix.setAmount(Money.of(new BigDecimal("100.00")));
        pix.setReceivedAt(Instant.now());
        pix.setSource(PixSource.STONE);
        return pix;
    }
}

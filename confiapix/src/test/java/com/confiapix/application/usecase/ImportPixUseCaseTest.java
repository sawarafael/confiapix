package com.confiapix.application.usecase;

import com.confiapix.application.service.NotificationService;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportPixUseCaseTest {

    @Mock
    private PixTransactionRepositoryPort pixTransactionRepository;

    @Mock
    private ReconciliationUseCase reconciliationUseCase;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ImportPixUseCase importPixUseCase;

    @Test
    void shouldSkipExistingPix() {
        UUID tenantId = UUID.randomUUID();
        PixTransaction candidate = buildPix("TX001");

        when(pixTransactionRepository.findByTenantIdAndTxid(tenantId, candidate.getTxid()))
                .thenReturn(Optional.of(candidate));

        var result = importPixUseCase.importAndReconcile(tenantId, candidate);

        assertThat(result.skipped()).isTrue();
        verify(pixTransactionRepository, never()).save(any());
    }

    @Test
    void shouldImportAndReconcileNewPix() {
        UUID tenantId = UUID.randomUUID();
        PixTransaction candidate = buildPix("TX002");
        PixTransaction saved = buildPix("TX002");
        saved.setId(UUID.randomUUID());

        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setStatus(ReconciliationStatus.MATCHED);

        when(pixTransactionRepository.findByTenantIdAndTxid(tenantId, candidate.getTxid())).thenReturn(Optional.empty());
        when(pixTransactionRepository.save(candidate)).thenReturn(saved);
        when(reconciliationUseCase.reconcile(saved)).thenReturn(reconciliation);

        var result = importPixUseCase.importAndReconcile(tenantId, candidate);

        assertThat(result.imported()).isTrue();
        assertThat(result.reconciled()).isTrue();
    }

    private PixTransaction buildPix(String txid) {
        PixTransaction pix = new PixTransaction();
        pix.setTxid(Txid.of(txid));
        pix.setAmount(Money.of(new BigDecimal("50.00")));
        pix.setReceivedAt(Instant.now());
        pix.setSource(PixSource.WEBHOOK);
        return pix;
    }
}

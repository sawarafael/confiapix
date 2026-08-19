package com.confiapix.application.usecase;

import com.confiapix.application.port.ReceivableLookupPort;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.domain.service.ReconciliationEngine;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconciliationUseCaseTest {

    @Mock
    private ReconciliationEngine reconciliationEngine;

    @Mock
    private ReconciliationRepositoryPort reconciliationRepository;

    @Mock
    private ReceivableLookupPort receivableLookupPort;

    @InjectMocks
    private ReconciliationUseCase reconciliationService;

    @Test
    void shouldReconcilePixWithMatchedStatus() {
        UUID tenantId = UUID.randomUUID();
        UUID pixId = UUID.randomUUID();
        UUID receivableId = UUID.randomUUID();
        Txid txid = Txid.of("TX123");

        PixTransaction pix = buildPix(tenantId, pixId, txid, "100.00");
        Reconciliation built = new Reconciliation();
        built.setStatus(ReconciliationStatus.MATCHED);

        when(reconciliationRepository.findByPixTransactionId(pixId)).thenReturn(Optional.empty());
        when(receivableLookupPort.findByTenantIdAndPixTxid(tenantId, txid))
                .thenReturn(Optional.of(new ReceivableLookupPort.ReceivableMatch(receivableId, new BigDecimal("100.00"))));
        when(reconciliationEngine.buildReconciliation(
                tenantId, pixId, receivableId, Money.of(new BigDecimal("100.00")), Money.of(new BigDecimal("100.00"))))
                .thenReturn(built);
        when(reconciliationRepository.save(any(Reconciliation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reconciliation result = reconciliationService.reconcile(pix);

        assertThat(result.getStatus()).isEqualTo(ReconciliationStatus.MATCHED);
        verify(reconciliationRepository).save(any(Reconciliation.class));
    }

    @Test
    void shouldRejectDuplicateReconciliation() {
        UUID pixId = UUID.randomUUID();
        PixTransaction pix = buildPix(UUID.randomUUID(), pixId, Txid.of("TX999"), "50.00");

        when(reconciliationRepository.findByPixTransactionId(pixId))
                .thenReturn(Optional.of(new Reconciliation()));

        assertThatThrownBy(() -> reconciliationService.reconcile(pix))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já conciliado");

        verify(reconciliationEngine, never()).buildReconciliation(any(), any(), any(), any(), any());
    }

    private PixTransaction buildPix(UUID tenantId, UUID pixId, Txid txid, String amount) {
        PixTransaction pix = new PixTransaction();
        pix.setId(pixId);
        pix.setTenantId(tenantId);
        pix.setTxid(txid);
        pix.setAmount(Money.of(new BigDecimal(amount)));
        pix.setReceivedAt(Instant.now());
        pix.setSource(PixSource.WEBHOOK);
        return pix;
    }
}

package com.confiapix.application.usecase;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.ReconciliationStatus;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.tenant.TenantContext;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconciliationQueryUseCaseTest {

    @Mock
    private ReconciliationRepositoryPort reconciliationRepository;

    @Mock
    private PixTransactionRepositoryPort pixTransactionRepository;

    @InjectMocks
    private ReconciliationQueryUseCase reconciliationQueryUseCase;

    private UUID tenantId;
    private UUID pixId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        pixId = UUID.randomUUID();
        TenantContextHolder.set(new TenantContext(tenantId, UUID.randomUUID(), "test@confiapix.test"));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldListReconciliations() {
        Reconciliation reconciliation = sampleReconciliation();
        PixTransaction pix = new PixTransaction();
        pix.setId(pixId);
        pix.setTenantId(tenantId);
        pix.setTxid(Txid.of("TX-REC"));

        when(reconciliationRepository.findByTenantId(tenantId)).thenReturn(List.of(reconciliation));
        when(pixTransactionRepository.findByIdAndTenantId(pixId, tenantId)).thenReturn(Optional.of(pix));

        var responses = reconciliationQueryUseCase.list();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getPixTxid()).isEqualTo("TX-REC");
    }

    @Test
    void shouldFindReconciliationById() {
        Reconciliation reconciliation = sampleReconciliation();
        when(reconciliationRepository.findByIdAndTenantId(reconciliation.getId(), tenantId))
                .thenReturn(Optional.of(reconciliation));
        when(pixTransactionRepository.findByIdAndTenantId(pixId, tenantId)).thenReturn(Optional.empty());

        var response = reconciliationQueryUseCase.findById(reconciliation.getId());

        assertThat(response.getStatus()).isEqualTo(ReconciliationStatus.MATCHED);
    }

    @Test
    void shouldThrowWhenReconciliationNotFound() {
        UUID id = UUID.randomUUID();
        when(reconciliationRepository.findByIdAndTenantId(id, tenantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reconciliationQueryUseCase.findById(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Conciliação não encontrada");
    }

    private Reconciliation sampleReconciliation() {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setId(UUID.randomUUID());
        reconciliation.setTenantId(tenantId);
        reconciliation.setPixTransactionId(pixId);
        reconciliation.setStatus(ReconciliationStatus.MATCHED);
        reconciliation.setExpectedAmount(Money.of(BigDecimal.TEN));
        reconciliation.setReceivedAmount(Money.of(BigDecimal.TEN));
        reconciliation.setReconciledAt(Instant.now());
        return reconciliation;
    }
}

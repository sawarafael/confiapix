package com.confiapix.domain.service;

import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.ReconciliationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReconciliationEngineTest {

    private ReconciliationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ReconciliationEngine();
    }

    @Test
    void shouldReturnMatchedWhenAmountsAreEqual() {
        UUID receivableId = UUID.randomUUID();
        Money expected = Money.of(new BigDecimal("150.00"));
        Money received = Money.of(new BigDecimal("150.00"));

        ReconciliationStatus status = engine.evaluate(receivableId, expected, received);

        assertThat(status).isEqualTo(ReconciliationStatus.MATCHED);
    }

    @Test
    void shouldReturnDivergentWhenAmountsDiffer() {
        UUID receivableId = UUID.randomUUID();
        Money expected = Money.of(new BigDecimal("150.00"));
        Money received = Money.of(new BigDecimal("149.99"));

        ReconciliationStatus status = engine.evaluate(receivableId, expected, received);

        assertThat(status).isEqualTo(ReconciliationStatus.DIVERGENT);
    }

    @Test
    void shouldReturnPendingWhenReceivableNotFound() {
        Money received = Money.of(new BigDecimal("150.00"));

        ReconciliationStatus status = engine.evaluate(null, null, received);

        assertThat(status).isEqualTo(ReconciliationStatus.PENDING);
    }

    @Test
    void shouldBuildReconciliationWithMatchedStatus() {
        UUID tenantId = UUID.randomUUID();
        UUID pixId = UUID.randomUUID();
        UUID receivableId = UUID.randomUUID();
        Money amount = Money.of(new BigDecimal("200.00"));

        var reconciliation = engine.buildReconciliation(tenantId, pixId, receivableId, amount, amount);

        assertThat(reconciliation.getStatus()).isEqualTo(ReconciliationStatus.MATCHED);
        assertThat(reconciliation.getTenantId()).isEqualTo(tenantId);
        assertThat(reconciliation.getPixTransactionId()).isEqualTo(pixId);
        assertThat(reconciliation.getReconciledAt()).isNotNull();
    }
}

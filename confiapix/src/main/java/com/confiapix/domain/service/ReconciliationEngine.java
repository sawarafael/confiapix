package com.confiapix.domain.service;

import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.ReconciliationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Motor de conciliação puro (sem dependências de framework).
 */
public class ReconciliationEngine {

    public ReconciliationStatus evaluate(UUID receivableId, Money expectedAmount, Money receivedAmount) {
        if (receivableId == null || expectedAmount == null) {
            return ReconciliationStatus.PENDING;
        }
        if (expectedAmount.matches(receivedAmount)) {
            return ReconciliationStatus.MATCHED;
        }
        return ReconciliationStatus.DIVERGENT;
    }

    public Reconciliation buildReconciliation(
            UUID tenantId,
            UUID pixTransactionId,
            UUID receivableId,
            Money expectedAmount,
            Money receivedAmount) {

        ReconciliationStatus status = evaluate(receivableId, expectedAmount, receivedAmount);

        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setTenantId(tenantId);
        reconciliation.setPixTransactionId(pixTransactionId);
        reconciliation.setReceivableId(receivableId);
        reconciliation.setExpectedAmount(expectedAmount);
        reconciliation.setReceivedAmount(receivedAmount);
        reconciliation.setStatus(status);
        reconciliation.setReconciledAt(status == ReconciliationStatus.PENDING ? null : Instant.now());
        return reconciliation;
    }
}

package com.confiapix.infrastructure.persistence.mapper;

import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.ReconciliationStatus;
import com.confiapix.infrastructure.persistence.entity.ReconciliationJpaEntity;

public final class ReconciliationPersistenceMapper {

    private ReconciliationPersistenceMapper() {
    }

    public static Reconciliation toDomain(ReconciliationJpaEntity entity) {
        Reconciliation domain = new Reconciliation();
        domain.setId(entity.getId());
        domain.setTenantId(entity.getTenantId());
        domain.setPixTransactionId(entity.getPixTransactionId());
        domain.setReceivableId(entity.getReceivableId());
        if (entity.getExpectedAmount() != null) {
            domain.setExpectedAmount(Money.of(entity.getExpectedAmount()));
        }
        domain.setReceivedAmount(Money.of(entity.getReceivedAmount()));
        domain.setStatus(ReconciliationStatus.valueOf(entity.getStatus()));
        domain.setReconciledAt(entity.getReconciledAt());
        domain.setNotes(entity.getNotes());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }

    public static ReconciliationJpaEntity toJpa(Reconciliation domain) {
        return ReconciliationJpaEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .pixTransactionId(domain.getPixTransactionId())
                .receivableId(domain.getReceivableId())
                .expectedAmount(domain.getExpectedAmount() != null ? domain.getExpectedAmount().amount() : null)
                .receivedAmount(domain.getReceivedAmount().amount())
                .status(domain.getStatus().name())
                .reconciledAt(domain.getReconciledAt())
                .notes(domain.getNotes())
                .build();
    }
}

package com.confiapix.domain.repository;

import com.confiapix.domain.entity.Reconciliation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReconciliationRepositoryPort {

    Reconciliation save(Reconciliation reconciliation);

    Optional<Reconciliation> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Reconciliation> findByPixTransactionId(UUID pixTransactionId);

    List<Reconciliation> findByTenantId(UUID tenantId);
}

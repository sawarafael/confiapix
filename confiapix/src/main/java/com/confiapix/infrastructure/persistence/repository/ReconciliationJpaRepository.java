package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.ReconciliationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReconciliationJpaRepository extends JpaRepository<ReconciliationJpaEntity, UUID> {

    Optional<ReconciliationJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<ReconciliationJpaEntity> findByPixTransactionId(UUID pixTransactionId);

    List<ReconciliationJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}

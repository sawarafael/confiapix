package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.PixTransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PixTransactionJpaRepository extends JpaRepository<PixTransactionJpaEntity, UUID> {

    Optional<PixTransactionJpaEntity> findByTenantIdAndTxid(UUID tenantId, String txid);

    Optional<PixTransactionJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<PixTransactionJpaEntity> findByTenantIdOrderByReceivedAtDesc(UUID tenantId);

    Page<PixTransactionJpaEntity> findByTenantIdOrderByReceivedAtDesc(UUID tenantId, Pageable pageable);
}

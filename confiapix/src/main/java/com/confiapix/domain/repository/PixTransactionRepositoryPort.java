package com.confiapix.domain.repository;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.valueobject.Txid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PixTransactionRepositoryPort {

    PixTransaction save(PixTransaction transaction);

    Optional<PixTransaction> findByTenantIdAndTxid(UUID tenantId, Txid txid);

    Optional<PixTransaction> findByIdAndTenantId(UUID id, UUID tenantId);

    List<PixTransaction> findByTenantId(UUID tenantId);

    Page<PixTransaction> findByTenantId(UUID tenantId, Pageable pageable);
}

package com.confiapix.infrastructure.persistence.adapter;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.persistence.mapper.PixPersistenceMapper;
import com.confiapix.infrastructure.persistence.repository.PixTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PixTransactionRepositoryAdapter implements PixTransactionRepositoryPort {

    private final PixTransactionJpaRepository jpaRepository;

    @Override
    public PixTransaction save(PixTransaction transaction) {
        var saved = jpaRepository.save(PixPersistenceMapper.toJpa(transaction));
        return PixPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<PixTransaction> findByTenantIdAndTxid(UUID tenantId, Txid txid) {
        return jpaRepository.findByTenantIdAndTxid(tenantId, txid.value())
                .map(PixPersistenceMapper::toDomain);
    }

    @Override
    public Optional<PixTransaction> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId)
                .map(PixPersistenceMapper::toDomain);
    }

    @Override
    public List<PixTransaction> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantIdOrderByReceivedAtDesc(tenantId).stream()
                .map(PixPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Page<PixTransaction> findByTenantId(UUID tenantId, Pageable pageable) {
        return jpaRepository.findByTenantIdOrderByReceivedAtDesc(tenantId, pageable)
                .map(PixPersistenceMapper::toDomain);
    }
}

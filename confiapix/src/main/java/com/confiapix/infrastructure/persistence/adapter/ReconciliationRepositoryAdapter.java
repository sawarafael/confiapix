package com.confiapix.infrastructure.persistence.adapter;

import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.infrastructure.persistence.mapper.ReconciliationPersistenceMapper;
import com.confiapix.infrastructure.persistence.repository.ReconciliationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReconciliationRepositoryAdapter implements ReconciliationRepositoryPort {

    private final ReconciliationJpaRepository jpaRepository;

    @Override
    public Reconciliation save(Reconciliation reconciliation) {
        var saved = jpaRepository.save(ReconciliationPersistenceMapper.toJpa(reconciliation));
        return ReconciliationPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Reconciliation> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId)
                .map(ReconciliationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Reconciliation> findByPixTransactionId(UUID pixTransactionId) {
        return jpaRepository.findByPixTransactionId(pixTransactionId)
                .map(ReconciliationPersistenceMapper::toDomain);
    }

    @Override
    public List<Reconciliation> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(ReconciliationPersistenceMapper::toDomain)
                .toList();
    }
}

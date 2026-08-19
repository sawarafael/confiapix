package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.StoneCredentialsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoneCredentialsJpaRepository extends JpaRepository<StoneCredentialsJpaEntity, UUID> {

    Optional<StoneCredentialsJpaEntity> findByTenantIdAndActiveTrue(UUID tenantId);

    Optional<StoneCredentialsJpaEntity> findByTenantId(UUID tenantId);

    List<StoneCredentialsJpaEntity> findByActiveTrue();

    Optional<StoneCredentialsJpaEntity> findFirstByAccountIdAndActiveTrue(String accountId);

    Optional<StoneCredentialsJpaEntity> findFirstByMerchantIdAndActiveTrue(String merchantId);
}

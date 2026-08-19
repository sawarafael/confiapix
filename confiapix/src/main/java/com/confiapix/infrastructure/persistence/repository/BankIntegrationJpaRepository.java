package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankIntegrationJpaRepository extends JpaRepository<BankIntegrationJpaEntity, UUID> {

    Optional<BankIntegrationJpaEntity> findByTenantIdAndProvider(UUID tenantId, String provider);

    Optional<BankIntegrationJpaEntity> findByTenantIdAndProviderAndActiveTrue(UUID tenantId, String provider);

    List<BankIntegrationJpaEntity> findByTenantId(UUID tenantId);

    List<BankIntegrationJpaEntity> findByActiveTrue();

    List<BankIntegrationJpaEntity> findByTenantIdAndActiveTrue(UUID tenantId);

    Optional<BankIntegrationJpaEntity> findFirstByProviderAndAccountRefAndActiveTrue(
            String provider, String accountRef);

    Optional<BankIntegrationJpaEntity> findFirstByProviderAndMerchantRefAndActiveTrue(
            String provider, String merchantRef);
}

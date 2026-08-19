package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Page<Company> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<Company> findByIdAndTenantId(UUID id, UUID tenantId);
}

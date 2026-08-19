package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query("SELECT c FROM Customer c WHERE c.company.tenant.id = :tenantId")
    Page<Customer> findByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.company.tenant.id = :tenantId")
    Optional<Customer> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}

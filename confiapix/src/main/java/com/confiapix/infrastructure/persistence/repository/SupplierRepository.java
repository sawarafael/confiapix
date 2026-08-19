package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    @Query("SELECT s FROM Supplier s WHERE s.company.tenant.id = :tenantId")
    Page<Supplier> findByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.company.tenant.id = :tenantId")
    Optional<Supplier> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}

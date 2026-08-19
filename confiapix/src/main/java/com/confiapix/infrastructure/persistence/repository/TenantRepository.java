package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
}

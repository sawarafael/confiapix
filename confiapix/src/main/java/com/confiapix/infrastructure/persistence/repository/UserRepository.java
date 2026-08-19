package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.User;
import com.confiapix.domain.valueobject.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findFirstByTenantIdAndRole(UUID tenantId, UserRole role);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}

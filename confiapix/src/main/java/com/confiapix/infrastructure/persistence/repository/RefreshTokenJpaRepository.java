package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHashAndRevokedFalse(String tokenHash);

    void deleteByUserId(UUID userId);
}

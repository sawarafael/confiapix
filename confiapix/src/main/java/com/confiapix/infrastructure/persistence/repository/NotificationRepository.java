package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.infrastructure.persistence.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    Optional<Notification> findByIdAndTenantId(UUID id, UUID tenantId);

    long countByTenantIdAndReadFalse(UUID tenantId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.tenantId = :tenantId AND n.read = false")
    int markAllAsRead(@Param("tenantId") UUID tenantId);
}

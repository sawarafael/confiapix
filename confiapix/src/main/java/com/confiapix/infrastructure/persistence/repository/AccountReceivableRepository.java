package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.domain.valueobject.AccountStatus;
import com.confiapix.infrastructure.persistence.entity.AccountReceivable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountReceivableRepository extends JpaRepository<AccountReceivable, UUID> {

    @Query("SELECT r FROM AccountReceivable r WHERE r.company.tenant.id = :tenantId")
    Page<AccountReceivable> findByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT r FROM AccountReceivable r WHERE r.id = :id AND r.company.tenant.id = :tenantId")
    Optional<AccountReceivable> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0) FROM AccountReceivable r
            WHERE r.company.tenant.id = :tenantId
            AND r.status IN :statuses
            """)
    BigDecimal sumAmountByTenantIdAndStatusIn(
            @Param("tenantId") UUID tenantId,
            @Param("statuses") List<AccountStatus> statuses);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0) FROM AccountReceivable r
            WHERE r.company.tenant.id = :tenantId
            AND r.status = 'PENDING' AND r.dueDate < :today
            """)
    BigDecimal sumOverduePending(@Param("tenantId") UUID tenantId, @Param("today") LocalDate today);

    @Query("""
            SELECT r FROM AccountReceivable r
            WHERE r.company.tenant.id = :tenantId
            AND r.pixTxid = :pixTxid
            """)
    Optional<AccountReceivable> findByTenantIdAndPixTxid(
            @Param("tenantId") UUID tenantId,
            @Param("pixTxid") String pixTxid);
}

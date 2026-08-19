package com.confiapix.infrastructure.persistence.repository;

import com.confiapix.domain.valueobject.AccountStatus;
import com.confiapix.infrastructure.persistence.entity.AccountPayable;
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

public interface AccountPayableRepository extends JpaRepository<AccountPayable, UUID> {

    @Query("SELECT p FROM AccountPayable p WHERE p.company.tenant.id = :tenantId")
    Page<AccountPayable> findByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT p FROM AccountPayable p WHERE p.id = :id AND p.company.tenant.id = :tenantId")
    Optional<AccountPayable> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM AccountPayable p
            WHERE p.company.tenant.id = :tenantId
            AND p.status IN :statuses
            """)
    BigDecimal sumAmountByTenantIdAndStatusIn(
            @Param("tenantId") UUID tenantId,
            @Param("statuses") List<AccountStatus> statuses);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM AccountPayable p
            WHERE p.company.tenant.id = :tenantId
            AND p.status = 'PENDING' AND p.dueDate < :today
            """)
    BigDecimal sumOverduePending(@Param("tenantId") UUID tenantId, @Param("today") LocalDate today);
}

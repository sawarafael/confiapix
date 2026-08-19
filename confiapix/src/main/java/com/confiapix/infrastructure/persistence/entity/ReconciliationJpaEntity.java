package com.confiapix.infrastructure.persistence.entity;

import com.confiapix.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reconciliations")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationJpaEntity extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "pix_transaction_id", nullable = false)
    private UUID pixTransactionId;

    @Column(name = "receivable_id")
    private UUID receivableId;

    @Column(name = "expected_amount", precision = 19, scale = 2)
    private BigDecimal expectedAmount;

    @Column(name = "received_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal receivedAmount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "reconciled_at")
    private Instant reconciledAt;

    @Column(length = 500)
    private String notes;
}

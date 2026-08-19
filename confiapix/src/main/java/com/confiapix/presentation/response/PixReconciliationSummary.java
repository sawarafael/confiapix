package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.ReconciliationStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PixReconciliationSummary {

    private UUID id;
    private ReconciliationStatus status;
    private BigDecimal expectedAmount;
    private BigDecimal receivedAmount;
    private Instant reconciledAt;
    private String notes;
}

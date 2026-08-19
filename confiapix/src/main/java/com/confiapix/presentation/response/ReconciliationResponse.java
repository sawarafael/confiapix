package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.ReconciliationStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ReconciliationResponse {

    private UUID id;
    private UUID pixTransactionId;
    private String pixTxid;
    private UUID receivableId;
    private BigDecimal expectedAmount;
    private BigDecimal receivedAmount;
    private ReconciliationStatus status;
    private Instant reconciledAt;
    private String notes;
    private Instant createdAt;
}

package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.AccountStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ReceivableResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private AccountStatus status;
    private UUID customerId;
    private String customerName;
    private UUID companyId;
    private String companyName;
    private Instant createdAt;
}

package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.PixSource;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PixResponse {

    private UUID id;
    private String txid;
    private String endToEndId;
    private BigDecimal amount;
    private String payerName;
    private String payerDocument;
    private Instant receivedAt;
    private PixSource source;
    private String provider;
    private UUID companyId;
    private Instant createdAt;
}

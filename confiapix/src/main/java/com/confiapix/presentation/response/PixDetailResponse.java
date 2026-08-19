package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.PixSource;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PixDetailResponse {

    private UUID id;
    private String txid;
    private String endToEndId;
    private BigDecimal amount;
    private Instant receivedAt;
    private PixSource source;
    private String provider;
    private UUID companyId;
    private Instant createdAt;
    private Instant updatedAt;

    /** ID interno do pagamento na Stone (campo `id` do payload). */
    private String stonePaymentId;

    /** Ex.: inbound_pix_payment */
    private String paymentType;

    /** Ex.: SETTLED */
    private String status;

    private Instant stoneCreatedAt;
    private Instant stoneSettledAt;

    /** account_id Stone do recebedor */
    private String stoneAccountId;

    /** Metadados de webhook Stone (quando origem WEBHOOK) */
    private String eventType;
    private String environment;
    private Instant eventHappenedAt;
    private Instant eventNotifiedAt;

    private PixPartyResponse payer;
    private PixPartyResponse receiver;
    private PixReconciliationSummary reconciliation;
}

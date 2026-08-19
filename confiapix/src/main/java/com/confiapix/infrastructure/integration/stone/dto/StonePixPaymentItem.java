package com.confiapix.infrastructure.integration.stone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StonePixPaymentItem(
        String id,
        String type,
        Long amount,
        String status,
        @JsonProperty("end_to_end_id") String endToEndId,
        @JsonProperty("transaction_id") String transactionId,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("settled_at") String settledAt,
        StonePixParty source,
        StonePixParty target) {

    public boolean isInboundReceived() {
        return "inbound_pix_payment".equals(type) && "SETTLED".equals(status);
    }
}

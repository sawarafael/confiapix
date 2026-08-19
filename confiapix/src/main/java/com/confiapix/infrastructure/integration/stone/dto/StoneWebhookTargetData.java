package com.confiapix.infrastructure.integration.stone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoneWebhookTargetData(
        @JsonProperty("account_id") String accountId,
        Long amount,
        String id,
        String status,
        @JsonProperty("end_to_end_id") String endToEndId,
        @JsonProperty("transaction_id") String transactionId,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("settled_at") String settledAt,
        @JsonProperty("counter_party") StoneWebhookCounterParty counterParty) {
}

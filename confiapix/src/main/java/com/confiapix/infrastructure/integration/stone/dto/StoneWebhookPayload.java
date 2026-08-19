package com.confiapix.infrastructure.integration.stone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoneWebhookPayload(
        String env,
        @JsonProperty("event_type") String eventType,
        String id,
        @JsonProperty("event_happened_at") String eventHappenedAt,
        @JsonProperty("event_notified_at") String eventNotifiedAt,
        @JsonProperty("target_data") StoneWebhookTargetData targetData,
        @JsonProperty("encrypted_body") String encryptedBody) {

    public boolean isPixInboundEvent() {
        if (eventType == null) {
            return false;
        }
        return eventType.equals("inbound_pix_payment")
                || eventType.equals("pix_inbound_payment_received");
    }
}

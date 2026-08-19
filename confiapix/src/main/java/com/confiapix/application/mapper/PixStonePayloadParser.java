package com.confiapix.application.mapper;

import com.confiapix.presentation.response.PixPartyResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PixStonePayloadParser {

    private final ObjectMapper objectMapper;

    public ParsedStonePayload parse(String rawPayload) {
        if (rawPayload == null || rawPayload.isBlank()) {
            return ParsedStonePayload.empty();
        }

        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            if (root.has("target_data")) {
                return parseWebhook(root);
            }
            return parsePaymentItem(root);
        } catch (Exception e) {
            return ParsedStonePayload.empty();
        }
    }

    private ParsedStonePayload parsePaymentItem(JsonNode root) {
        ParsedStonePayload.ParsedStonePayloadBuilder builder = ParsedStonePayload.builder()
                .stonePaymentId(text(root, "id"))
                .paymentType(text(root, "type"))
                .status(text(root, "status"))
                .stoneCreatedAt(instant(text(root, "created_at")))
                .stoneSettledAt(instant(text(root, "settled_at")))
                .payer(party(root.path("source")))
                .receiver(party(root.path("target")));

        return builder.build();
    }

    private ParsedStonePayload parseWebhook(JsonNode root) {
        JsonNode data = root.path("target_data");

        return ParsedStonePayload.builder()
                .stonePaymentId(text(data, "id"))
                .status(text(data, "status"))
                .stoneCreatedAt(instant(text(data, "created_at")))
                .stoneSettledAt(instant(text(data, "settled_at")))
                .stoneAccountId(text(data, "account_id"))
                .eventType(text(root, "event_type"))
                .environment(text(root, "env"))
                .eventHappenedAt(instant(text(root, "event_happened_at")))
                .eventNotifiedAt(instant(text(root, "event_notified_at")))
                .paymentType(text(root, "event_type"))
                .payer(partyFromCounterParty(data.path("counter_party")))
                .build();
    }

    private PixPartyResponse party(JsonNode partyNode) {
        JsonNode entity = partyNode.path("entity");
        if (entity.isMissingNode() || entity.isNull()) {
            return null;
        }
        return PixPartyResponse.builder()
                .name(text(entity, "name"))
                .document(text(entity, "document"))
                .documentType(text(entity, "document_type"))
                .build();
    }

    private PixPartyResponse partyFromCounterParty(JsonNode counterParty) {
        return party(counterParty);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text.isBlank() ? null : text;
    }

    private Instant instant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Instant.parse(value);
    }

    @lombok.Builder
    @lombok.Getter
    public static class ParsedStonePayload {
        private final String stonePaymentId;
        private final String paymentType;
        private final String status;
        private final Instant stoneCreatedAt;
        private final Instant stoneSettledAt;
        private final String stoneAccountId;
        private final String eventType;
        private final String environment;
        private final Instant eventHappenedAt;
        private final Instant eventNotifiedAt;
        private final PixPartyResponse payer;
        private final PixPartyResponse receiver;

        static ParsedStonePayload empty() {
            return ParsedStonePayload.builder().build();
        }
    }
}

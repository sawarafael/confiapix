package com.confiapix.infrastructure.integration.stone.service;

import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoneWebhookPayloadResolver {

    private final ObjectMapper objectMapper;
    private final StoneWebhookDecryptionService decryptionService;

    public StoneWebhookPayload resolve(JsonNode body) {
        if (body.hasNonNull("encrypted_body")) {
            String encryptedBody = body.get("encrypted_body").asText();
            if (!encryptedBody.isBlank()) {
                return decryptionService.decrypt(encryptedBody);
            }
        }

        StoneWebhookPayload payload = objectMapper.convertValue(body, StoneWebhookPayload.class);
        if (payload.encryptedBody() != null && !payload.encryptedBody().isBlank()) {
            return decryptionService.decrypt(payload.encryptedBody());
        }
        return payload;
    }
}

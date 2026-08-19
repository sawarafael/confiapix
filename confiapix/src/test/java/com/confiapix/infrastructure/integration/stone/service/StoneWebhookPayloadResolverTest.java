package com.confiapix.infrastructure.integration.stone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoneWebhookPayloadResolverTest {

    @Mock
    private StoneWebhookDecryptionService decryptionService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StoneWebhookPayloadResolver resolver;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        resolver = new StoneWebhookPayloadResolver(objectMapper, decryptionService);
    }

    @Test
    void shouldResolvePlainPayload() throws Exception {
        JsonNode body = objectMapper.readTree("""
                {"event_type":"pix_inbound_payment_received","target_data":{"account_id":"acc-1"}}
                """);

        var payload = resolver.resolve(body);

        assertThat(payload.eventType()).isEqualTo("pix_inbound_payment_received");
        assertThat(payload.targetData().accountId()).isEqualTo("acc-1");
    }

    @Test
    void shouldResolveEncryptedPayload() throws Exception {
        JsonNode body = objectMapper.readTree("""
                {"encrypted_body":"jwe.token.here"}
                """);
        when(decryptionService.decrypt("jwe.token.here"))
                .thenReturn(new com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload(
                        "sandbox", "inbound_pix_payment", "evt", null, null, null, null));

        var payload = resolver.resolve(body);

        assertThat(payload.eventType()).isEqualTo("inbound_pix_payment");
    }
}

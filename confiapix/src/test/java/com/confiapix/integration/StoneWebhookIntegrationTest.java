package com.confiapix.integration;

import com.confiapix.support.IntegrationTestSupport;
import com.confiapix.support.StoneWebhookJweTestHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StoneWebhookIntegrationTest {

    private static final StoneWebhookJweTestHelper.KeyMaterial MERCHANT_KEYS;
    private static final StoneWebhookJweTestHelper.KeyMaterial STONE_KEYS;

    static {
        try {
            MERCHANT_KEYS = StoneWebhookJweTestHelper.generateKeyPair();
            STONE_KEYS = StoneWebhookJweTestHelper.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @DynamicPropertySource
    static void webhookKeys(DynamicPropertyRegistry registry) {
        registry.add("confiapix.stone.webhook-private-key-pem", () -> MERCHANT_KEYS.privateKeyPem());
        registry.add("confiapix.stone.webhook-public-key-pem", () -> STONE_KEYS.publicKeyPem());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldProcessPlainWebhookAndExposePixViaApi() throws Exception {
        String suffix = "plain-webhook";
        IntegrationTestSupport.AuthTokens auth = IntegrationTestSupport.registerAdmin(mockMvc, objectMapper, suffix);
        String accountId = "account-" + suffix;
        IntegrationTestSupport.saveStoneCredentials(mockMvc, objectMapper, auth.token(), accountId);

        String webhookBody = """
                {
                  "env": "sandbox",
                  "event_type": "pix_inbound_payment_received",
                  "id": "evt-int-1",
                  "target_data": {
                    "account_id": "%s",
                    "amount": 15000,
                    "id": "pix-int-1",
                    "status": "SETTLED",
                    "end_to_end_id": "EINT001",
                    "transaction_id": "TX-INT-PLAIN",
                    "settled_at": "2024-06-10T12:00:00Z"
                  }
                }
                """.formatted(accountId);

        MvcResult webhookResult = mockMvc.perform(post("/api/v1/webhooks/stone/pix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode webhookResponse = IntegrationTestSupport.readJson(mockMvc, objectMapper, webhookResult);
        assertThat(webhookResponse.get("success").asBoolean()).isTrue();
        assertThat(webhookResponse.get("data").get("processed").asBoolean()).isTrue();
        assertThat(webhookResponse.get("data").get("imported").asBoolean()).isTrue();
        assertThat(webhookResponse.get("data").get("txid").asText()).isEqualTo("TX-INT-PLAIN");

        MvcResult pixResult = mockMvc.perform(get("/api/v1/pix/TX-INT-PLAIN")
                        .header("Authorization", "Bearer " + auth.token()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pixResponse = IntegrationTestSupport.readJson(mockMvc, objectMapper, pixResult);
        assertThat(pixResponse.get("data").get("txid").asText()).isEqualTo("TX-INT-PLAIN");
    }

    @Test
    void shouldProcessEncryptedWebhook() throws Exception {
        String suffix = "encrypted-webhook";
        IntegrationTestSupport.AuthTokens auth = IntegrationTestSupport.registerAdmin(mockMvc, objectMapper, suffix);
        String accountId = "account-" + suffix;
        IntegrationTestSupport.saveStoneCredentials(mockMvc, objectMapper, auth.token(), accountId);

        String encrypted = StoneWebhookJweTestHelper.encryptWebhook(MERCHANT_KEYS, STONE_KEYS, Map.of(
                "env", "sandbox",
                "event_type", "inbound_pix_payment",
                "id", "evt-enc-int",
                "target_data", Map.of(
                        "account_id", accountId,
                        "amount", 9900,
                        "transaction_id", "TX-INT-ENC",
                        "end_to_end_id", "EINTENC",
                        "status", "SETTLED",
                        "settled_at", "2024-06-10T13:00:00Z")));

        String body = objectMapper.writeValueAsString(Map.of("encrypted_body", encrypted));

        MvcResult webhookResult = mockMvc.perform(post("/api/v1/webhooks/stone/pix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode webhookResponse = IntegrationTestSupport.readJson(mockMvc, objectMapper, webhookResult);
        assertThat(webhookResponse.get("data").get("imported").asBoolean()).isTrue();
        assertThat(webhookResponse.get("data").get("txid").asText()).isEqualTo("TX-INT-ENC");
    }
}

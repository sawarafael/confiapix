package com.confiapix.infrastructure.integration.stone.service;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.confiapix.support.StoneWebhookJweTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoneWebhookDecryptionServiceTest {

    private StoneWebhookJweTestHelper.KeyMaterial merchantKeys;
    private StoneWebhookJweTestHelper.KeyMaterial stoneKeys;
    private StoneWebhookDecryptionService decryptionService;

    @BeforeEach
    void setUp() throws Exception {
        merchantKeys = StoneWebhookJweTestHelper.generateKeyPair();
        stoneKeys = StoneWebhookJweTestHelper.generateKeyPair();

        StoneProperties properties = new StoneProperties();
        properties.setWebhookPrivateKeyPem(merchantKeys.privateKeyPem());
        properties.setWebhookPublicKeyPem(stoneKeys.publicKeyPem());

        decryptionService = new StoneWebhookDecryptionService(properties, new ObjectMapper());
    }

    @Test
    void shouldDecryptEncryptedWebhook() throws Exception {
        String encrypted = StoneWebhookJweTestHelper.encryptWebhook(merchantKeys, stoneKeys, Map.of(
                "env", "sandbox",
                "event_type", "pix_inbound_payment_received",
                "id", "evt-encrypted",
                "target_data", Map.of(
                        "account_id", "account-enc",
                        "amount", 5000,
                        "transaction_id", "TX-ENC-1",
                        "end_to_end_id", "E999",
                        "status", "SETTLED")));

        StoneWebhookPayload payload = decryptionService.decrypt(encrypted);

        assertThat(payload.eventType()).isEqualTo("pix_inbound_payment_received");
        assertThat(payload.targetData().accountId()).isEqualTo("account-enc");
        assertThat(payload.targetData().transactionId()).isEqualTo("TX-ENC-1");
    }

    @Test
    void shouldRejectWhenPrivateKeyNotConfigured() {
        StoneProperties properties = new StoneProperties();
        StoneWebhookDecryptionService service = new StoneWebhookDecryptionService(properties, new ObjectMapper());

        assertThatThrownBy(() -> service.decrypt("invalid.jwe.token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("chave privada RSA");
    }

    @Test
    void shouldRejectInvalidSignatureWhenPublicKeyConfigured() throws Exception {
        StoneWebhookJweTestHelper.KeyMaterial wrongSigner = StoneWebhookJweTestHelper.generateKeyPair();
        String encrypted = StoneWebhookJweTestHelper.encryptWebhook(merchantKeys, wrongSigner, Map.of(
                "event_type", "pix_inbound_payment_received"));

        assertThatThrownBy(() -> decryptionService.decrypt(encrypted))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Assinatura");
    }
}

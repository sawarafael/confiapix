package com.confiapix.infrastructure.integration.stone.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookCounterParty;
import com.confiapix.infrastructure.integration.stone.dto.StonePixEntity;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookTargetData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StoneWebhookMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldMapWebhookToPixTransaction() {
        UUID tenantId = UUID.randomUUID();
        StoneWebhookPayload payload = new StoneWebhookPayload(
                "sandbox",
                "pix_inbound_payment_received",
                "evt-1",
                "2024-06-10T12:00:00Z",
                null,
                new StoneWebhookTargetData(
                        "acc-1",
                        25075L,
                        "internal-id",
                        "SETTLED",
                        "E999",
                        "TX-WH-99",
                        "2024-06-10T12:00:00Z",
                        "2024-06-10T12:00:01Z",
                        new StoneWebhookCounterParty(new StonePixEntity("98765432100", "Pedro", "cpf"))),
                null);

        PixTransaction pix = StoneWebhookMapper.toDomain(tenantId, payload, objectMapper);

        assertThat(pix.getTenantId()).isEqualTo(tenantId);
        assertThat(pix.getTxid().value()).isEqualTo("TX-WH-99");
        assertThat(pix.getAmount().amount().toPlainString()).isEqualTo("250.75");
        assertThat(pix.getPayerName()).isEqualTo("Pedro");
        assertThat(pix.getSource()).isEqualTo(PixSource.WEBHOOK);
    }
}

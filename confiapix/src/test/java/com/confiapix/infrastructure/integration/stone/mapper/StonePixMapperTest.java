package com.confiapix.infrastructure.integration.stone.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.infrastructure.integration.stone.dto.StonePixEntity;
import com.confiapix.infrastructure.integration.stone.dto.StonePixParty;
import com.confiapix.infrastructure.integration.stone.dto.StonePixPaymentItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StonePixMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldMapInboundPixToDomain() {
        UUID tenantId = UUID.randomUUID();
        StonePixPaymentItem item = new StonePixPaymentItem(
                "pix-id",
                "inbound_pix_payment",
                15050L,
                "SETTLED",
                "E165015552021062517465eeb40dd2f6",
                "TX123456",
                "2024-01-15T10:00:00Z",
                "2024-01-15T10:00:01Z",
                new StonePixParty(new StonePixEntity("12345678901", "João Silva", "cpf")),
                null);

        PixTransaction pix = StonePixMapper.toDomain(tenantId, item, objectMapper);

        assertThat(pix.getTenantId()).isEqualTo(tenantId);
        assertThat(pix.getTxid().value()).isEqualTo("TX123456");
        assertThat(pix.getAmount().amount().toPlainString()).isEqualTo("150.50");
        assertThat(pix.getPayerName()).isEqualTo("João Silva");
        assertThat(pix.getPayerDocument()).isEqualTo("12345678901");
        assertThat(pix.getSource()).isEqualTo(PixSource.STONE);
    }
}

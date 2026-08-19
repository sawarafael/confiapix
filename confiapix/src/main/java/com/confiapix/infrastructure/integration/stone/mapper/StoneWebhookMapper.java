package com.confiapix.infrastructure.integration.stone.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookTargetData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public final class StoneWebhookMapper {

    private StoneWebhookMapper() {
    }

    public static PixTransaction toDomain(UUID tenantId, StoneWebhookPayload payload, ObjectMapper objectMapper) {
        StoneWebhookTargetData data = payload.targetData();
        PixTransaction pix = new PixTransaction();
        pix.setTenantId(tenantId);
        pix.setTxid(Txid.of(resolveTxid(data)));
        pix.setEndToEndId(data.endToEndId());
        pix.setAmount(Money.of(centsToDecimal(data.amount())));
        if (data.counterParty() != null && data.counterParty().entity() != null) {
            pix.setPayerName(data.counterParty().entity().name());
            pix.setPayerDocument(data.counterParty().entity().document());
        }
        pix.setReceivedAt(resolveReceivedAt(payload, data));
        pix.setSource(PixSource.WEBHOOK);
        pix.setProvider(BankProviderCodes.STONE);
        pix.setRawPayload(toJson(payload, objectMapper));
        return pix;
    }

    private static String resolveTxid(StoneWebhookTargetData data) {
        if (data.transactionId() != null && !data.transactionId().isBlank()) {
            return data.transactionId().trim();
        }
        if (data.endToEndId() != null && !data.endToEndId().isBlank()) {
            return data.endToEndId().trim();
        }
        return data.id();
    }

    private static BigDecimal centsToDecimal(Long amountCents) {
        if (amountCents == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amountCents).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private static Instant resolveReceivedAt(StoneWebhookPayload payload, StoneWebhookTargetData data) {
        String timestamp = data.settledAt() != null ? data.settledAt()
                : data.createdAt() != null ? data.createdAt()
                : payload.eventHappenedAt();
        return timestamp != null ? Instant.parse(timestamp) : Instant.now();
    }

    private static String toJson(StoneWebhookPayload payload, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

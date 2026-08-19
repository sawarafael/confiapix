package com.confiapix.infrastructure.integration.stone.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.integration.stone.dto.StonePixPaymentItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public final class StonePixMapper {

    private StonePixMapper() {
    }

    public static PixTransaction toDomain(UUID tenantId, StonePixPaymentItem item, ObjectMapper objectMapper) {
        PixTransaction pix = new PixTransaction();
        pix.setTenantId(tenantId);
        pix.setTxid(Txid.of(resolveTxid(item)));
        pix.setEndToEndId(item.endToEndId());
        pix.setAmount(Money.of(centsToDecimal(item.amount())));
        pix.setPayerName(extractPayerName(item));
        pix.setPayerDocument(extractPayerDocument(item));
        pix.setReceivedAt(resolveReceivedAt(item));
        pix.setSource(PixSource.SYNC);
        pix.setProvider(BankProviderCodes.STONE);
        pix.setRawPayload(toJson(item, objectMapper));
        return pix;
    }

    public static String resolveTxid(StonePixPaymentItem item) {
        if (item.transactionId() != null && !item.transactionId().isBlank()) {
            return item.transactionId().trim();
        }
        if (item.endToEndId() != null && !item.endToEndId().isBlank()) {
            return item.endToEndId().trim();
        }
        return item.id();
    }

    private static BigDecimal centsToDecimal(Long amountCents) {
        if (amountCents == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amountCents).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private static String extractPayerName(StonePixPaymentItem item) {
        if (item.source() != null && item.source().entity() != null) {
            return item.source().entity().name();
        }
        return null;
    }

    private static String extractPayerDocument(StonePixPaymentItem item) {
        if (item.source() != null && item.source().entity() != null) {
            return item.source().entity().document();
        }
        return null;
    }

    private static Instant resolveReceivedAt(StonePixPaymentItem item) {
        String timestamp = item.settledAt() != null ? item.settledAt() : item.createdAt();
        return timestamp != null ? Instant.parse(timestamp) : Instant.now();
    }

    private static String toJson(StonePixPaymentItem item, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

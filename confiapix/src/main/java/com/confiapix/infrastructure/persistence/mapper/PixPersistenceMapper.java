package com.confiapix.infrastructure.persistence.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.persistence.entity.PixTransactionJpaEntity;

public final class PixPersistenceMapper {

    private PixPersistenceMapper() {
    }

    public static PixTransaction toDomain(PixTransactionJpaEntity entity) {
        PixTransaction domain = new PixTransaction();
        domain.setId(entity.getId());
        domain.setTenantId(entity.getTenantId());
        domain.setCompanyId(entity.getCompanyId());
        domain.setTxid(Txid.of(entity.getTxid()));
        domain.setEndToEndId(entity.getEndToEndId());
        domain.setAmount(Money.of(entity.getAmount()));
        domain.setPayerName(entity.getPayerName());
        domain.setPayerDocument(entity.getPayerDocument());
        domain.setReceivedAt(entity.getReceivedAt());
        domain.setSource(PixSource.valueOf(entity.getSource()));
        domain.setProvider(entity.getProvider() != null ? entity.getProvider() : BankProviderCodes.STONE);
        domain.setRawPayload(entity.getRawPayload());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }

    public static PixTransactionJpaEntity toJpa(PixTransaction domain) {
        return PixTransactionJpaEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .companyId(domain.getCompanyId())
                .txid(domain.getTxid().value())
                .endToEndId(domain.getEndToEndId())
                .amount(domain.getAmount().amount())
                .payerName(domain.getPayerName())
                .payerDocument(domain.getPayerDocument())
                .receivedAt(domain.getReceivedAt())
                .source(domain.getSource().name())
                .provider(domain.getProvider() != null ? domain.getProvider() : BankProviderCodes.STONE)
                .rawPayload(domain.getRawPayload())
                .build();
    }
}

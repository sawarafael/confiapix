package com.confiapix.application.port;

import com.confiapix.domain.valueobject.Txid;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ReceivableLookupPort {

    Optional<ReceivableMatch> findByTenantIdAndPixTxid(UUID tenantId, Txid txid);

    record ReceivableMatch(UUID id, BigDecimal amount) {
    }
}

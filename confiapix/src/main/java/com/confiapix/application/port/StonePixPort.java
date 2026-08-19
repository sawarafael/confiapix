package com.confiapix.application.port;

import com.confiapix.domain.entity.PixTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StonePixPort {

    Optional<PixTransaction> findPixByTxid(UUID tenantId, String txid);

    List<PixTransaction> findRecentPix(UUID tenantId);

    List<PixTransaction> findRecentPix(UUID tenantId, int limit);
}

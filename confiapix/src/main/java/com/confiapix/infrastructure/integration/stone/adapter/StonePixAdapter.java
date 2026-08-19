package com.confiapix.infrastructure.integration.stone.adapter;

import com.confiapix.application.port.StonePixPort;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.infrastructure.integration.stone.client.StonePixClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StonePixAdapter implements StonePixPort {

    private final StonePixClient stonePixClient;

    @Override
    public Optional<PixTransaction> findPixByTxid(UUID tenantId, String txid) {
        return stonePixClient.findPixByTxid(tenantId, txid);
    }

    @Override
    public List<PixTransaction> findRecentPix(UUID tenantId) {
        return stonePixClient.findRecentPix(tenantId);
    }

    @Override
    public List<PixTransaction> findRecentPix(UUID tenantId, int limit) {
        return stonePixClient.findRecentPix(tenantId, limit);
    }
}

package com.confiapix.infrastructure.persistence.adapter;

import com.confiapix.application.port.ReceivableLookupPort;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.persistence.repository.AccountReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReceivableLookupAdapter implements ReceivableLookupPort {

    private final AccountReceivableRepository accountReceivableRepository;

    @Override
    public Optional<ReceivableMatch> findByTenantIdAndPixTxid(UUID tenantId, Txid txid) {
        return accountReceivableRepository.findByTenantIdAndPixTxid(tenantId, txid.value())
                .map(receivable -> new ReceivableMatch(receivable.getId(), receivable.getAmount()));
    }
}

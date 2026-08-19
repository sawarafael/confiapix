package com.confiapix.application.usecase;

import com.confiapix.application.port.ReceivableLookupPort;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.domain.service.ReconciliationEngine;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.ReconciliationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReconciliationUseCase {

    private final ReconciliationEngine reconciliationEngine;
    private final ReconciliationRepositoryPort reconciliationRepository;
    private final ReceivableLookupPort receivableLookupPort;

    @Transactional
    public Reconciliation reconcile(PixTransaction pixTransaction) {
        if (reconciliationRepository.findByPixTransactionId(pixTransaction.getId()).isPresent()) {
            throw new BusinessException("PIX já conciliado: " + pixTransaction.getTxid().value());
        }

        var receivableMatch = receivableLookupPort
                .findByTenantIdAndPixTxid(pixTransaction.getTenantId(), pixTransaction.getTxid())
                .orElse(null);

        UUID receivableId = receivableMatch != null ? receivableMatch.id() : null;
        Money expectedAmount = receivableMatch != null ? Money.of(receivableMatch.amount()) : null;
        Money receivedAmount = pixTransaction.getAmount();

        Reconciliation reconciliation = reconciliationEngine.buildReconciliation(
                pixTransaction.getTenantId(),
                pixTransaction.getId(),
                receivableId,
                expectedAmount,
                receivedAmount);

        if (reconciliation.getStatus() == ReconciliationStatus.MATCHED) {
            reconciliation.setNotes("Conciliação automática por TXID e valor");
        } else if (reconciliation.getStatus() == ReconciliationStatus.DIVERGENT) {
            reconciliation.setNotes("Valor recebido diverge do valor esperado da cobrança");
        } else {
            reconciliation.setNotes("Cobrança não encontrada para o TXID informado");
        }

        return reconciliationRepository.save(reconciliation);
    }

    @Transactional(readOnly = true)
    public Reconciliation findById(UUID tenantId, UUID reconciliationId) {
        return reconciliationRepository.findByIdAndTenantId(reconciliationId, tenantId)
                .orElseThrow(() -> new BusinessException("Conciliação não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Reconciliation> listByTenant(UUID tenantId) {
        return reconciliationRepository.findByTenantId(tenantId);
    }
}

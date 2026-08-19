package com.confiapix.application.usecase;

import com.confiapix.application.service.NotificationService;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportPixUseCase {

    private final PixTransactionRepositoryPort pixTransactionRepository;
    private final ReconciliationUseCase reconciliationUseCase;
    private final NotificationService notificationService;

    @Transactional
    public ImportPixResult importAndReconcile(UUID tenantId, PixTransaction candidate) {
        if (pixTransactionRepository.findByTenantIdAndTxid(tenantId, candidate.getTxid()).isPresent()) {
            return ImportPixResult.duplicate();
        }

        candidate.setTenantId(tenantId);
        PixTransaction saved = pixTransactionRepository.save(candidate);
        notificationService.notifyPixImported(saved);

        Optional<Reconciliation> reconciliation = Optional.empty();
        try {
            reconciliation = Optional.of(reconciliationUseCase.reconcile(saved));
            notificationService.notifyReconciliation(reconciliation.get(), saved.getTxid().value());
        } catch (Exception ex) {
            log.warn("Falha ao conciliar PIX {} tenant {}: {}", candidate.getTxid().value(), tenantId, ex.getMessage());
        }

        return ImportPixResult.imported(reconciliation);
    }

    public record ImportPixResult(boolean imported, boolean skipped, Optional<Reconciliation> reconciliation) {

        public static ImportPixResult duplicate() {
            return new ImportPixResult(false, true, Optional.empty());
        }

        public static ImportPixResult imported(Optional<Reconciliation> reconciliation) {
            return new ImportPixResult(true, false, reconciliation);
        }

        public boolean reconciled() {
            return reconciliation.isPresent();
        }
    }
}

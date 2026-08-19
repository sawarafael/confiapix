package com.confiapix.application.service;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.valueobject.NotificationType;
import com.confiapix.domain.valueobject.ReconciliationStatus;
import com.confiapix.infrastructure.persistence.entity.Notification;
import com.confiapix.infrastructure.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notifyPixImported(PixTransaction pix) {
        notificationRepository.save(Notification.builder()
                .tenantId(pix.getTenantId())
                .type(NotificationType.PIX_RECEIVED)
                .title("PIX recebido")
                .message("Pagamento de %s recebido (TXID %s).".formatted(
                        formatAmount(pix.getAmount().amount()), pix.getTxid().value()))
                .referenceId(pix.getId())
                .referenceType("PIX")
                .read(false)
                .build());
    }

    @Transactional
    public void notifyReconciliation(Reconciliation reconciliation, String txid) {
        NotificationType type = switch (reconciliation.getStatus()) {
            case MATCHED -> NotificationType.RECONCILIATION_MATCHED;
            case DIVERGENT -> NotificationType.RECONCILIATION_DIVERGENT;
            case PENDING -> NotificationType.RECONCILIATION_PENDING;
        };

        String title = switch (type) {
            case RECONCILIATION_MATCHED -> "Conciliação confirmada";
            case RECONCILIATION_DIVERGENT -> "Divergência na conciliação";
            case RECONCILIATION_PENDING -> "Conciliação pendente";
            default -> "Conciliação atualizada";
        };

        String message = switch (type) {
            case RECONCILIATION_MATCHED ->
                    "PIX %s conciliado com sucesso (%s).".formatted(
                            txid, formatAmount(reconciliation.getReceivedAmount().amount()));
            case RECONCILIATION_DIVERGENT ->
                    "PIX %s com valor divergente do esperado.".formatted(txid);
            case RECONCILIATION_PENDING ->
                    "PIX %s aguardando vínculo com cobrança.".formatted(txid);
            default -> reconciliation.getNotes();
        };

        notificationRepository.save(Notification.builder()
                .tenantId(reconciliation.getTenantId())
                .type(type)
                .title(title)
                .message(message)
                .referenceId(reconciliation.getId())
                .referenceType("RECONCILIATION")
                .read(false)
                .build());
    }

    @Transactional
    public void notifyStoneSync(UUID tenantId, int importedCount) {
        if (importedCount <= 0) {
            return;
        }

        notificationRepository.save(Notification.builder()
                .tenantId(tenantId)
                .type(NotificationType.STONE_SYNC)
                .title("Sincronização Stone")
                .message(importedCount == 1
                        ? "1 novo PIX importado da Stone."
                        : "%d novos PIX importados da Stone.".formatted(importedCount))
                .referenceType("STONE_SYNC")
                .read(false)
                .build());
    }

    private String formatAmount(BigDecimal amount) {
        return CURRENCY.format(amount);
    }
}

package com.confiapix.application.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.presentation.response.PixDetailResponse;
import com.confiapix.presentation.response.PixPartyResponse;
import com.confiapix.presentation.response.PixReconciliationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PixDetailMapper {

    private final PixStonePayloadParser stonePayloadParser;

    public PixDetailResponse toDetail(PixTransaction pix, Reconciliation reconciliation) {
        PixStonePayloadParser.ParsedStonePayload stone = stonePayloadParser.parse(pix.getRawPayload());

        PixPartyResponse payer = mergeParty(
                stone.getPayer(),
                pix.getPayerName(),
                pix.getPayerDocument(),
                null);

        return PixDetailResponse.builder()
                .id(pix.getId())
                .txid(pix.getTxid().value())
                .endToEndId(pix.getEndToEndId())
                .amount(pix.getAmount().amount())
                .receivedAt(pix.getReceivedAt())
                .source(pix.getSource())
                .provider(pix.getProvider())
                .companyId(pix.getCompanyId())
                .createdAt(pix.getCreatedAt())
                .updatedAt(pix.getUpdatedAt())
                .stonePaymentId(stone.getStonePaymentId())
                .paymentType(stone.getPaymentType())
                .status(stone.getStatus())
                .stoneCreatedAt(stone.getStoneCreatedAt())
                .stoneSettledAt(stone.getStoneSettledAt())
                .stoneAccountId(stone.getStoneAccountId())
                .eventType(stone.getEventType())
                .environment(stone.getEnvironment())
                .eventHappenedAt(stone.getEventHappenedAt())
                .eventNotifiedAt(stone.getEventNotifiedAt())
                .payer(payer)
                .receiver(stone.getReceiver())
                .reconciliation(toReconciliationSummary(reconciliation))
                .build();
    }

    private PixPartyResponse mergeParty(
            PixPartyResponse fromPayload,
            String fallbackName,
            String fallbackDocument,
            String fallbackDocumentType) {
        if (fromPayload != null) {
            return PixPartyResponse.builder()
                    .name(firstNonBlank(fromPayload.getName(), fallbackName))
                    .document(firstNonBlank(fromPayload.getDocument(), fallbackDocument))
                    .documentType(firstNonBlank(fromPayload.getDocumentType(), fallbackDocumentType))
                    .build();
        }
        if (fallbackName == null && fallbackDocument == null) {
            return null;
        }
        return PixPartyResponse.builder()
                .name(fallbackName)
                .document(fallbackDocument)
                .documentType(fallbackDocumentType)
                .build();
    }

    private PixReconciliationSummary toReconciliationSummary(Reconciliation reconciliation) {
        if (reconciliation == null) {
            return null;
        }
        return PixReconciliationSummary.builder()
                .id(reconciliation.getId())
                .status(reconciliation.getStatus())
                .expectedAmount(reconciliation.getExpectedAmount() != null
                        ? reconciliation.getExpectedAmount().amount() : null)
                .receivedAmount(reconciliation.getReceivedAmount().amount())
                .reconciledAt(reconciliation.getReconciledAt())
                .notes(reconciliation.getNotes())
                .build();
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}

package com.confiapix.application.mapper;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.presentation.response.PixResponse;
import com.confiapix.presentation.response.ReconciliationResponse;

public final class PixResponseMapper {

    private PixResponseMapper() {
    }

    public static PixResponse toResponse(PixTransaction pix) {
        return PixResponse.builder()
                .id(pix.getId())
                .txid(pix.getTxid().value())
                .endToEndId(pix.getEndToEndId())
                .amount(pix.getAmount().amount())
                .payerName(pix.getPayerName())
                .payerDocument(pix.getPayerDocument())
                .receivedAt(pix.getReceivedAt())
                .source(pix.getSource())
                .provider(pix.getProvider())
                .companyId(pix.getCompanyId())
                .createdAt(pix.getCreatedAt())
                .build();
    }

    public static ReconciliationResponse toResponse(Reconciliation reconciliation, String pixTxid) {
        return ReconciliationResponse.builder()
                .id(reconciliation.getId())
                .pixTransactionId(reconciliation.getPixTransactionId())
                .pixTxid(pixTxid)
                .receivableId(reconciliation.getReceivableId())
                .expectedAmount(reconciliation.getExpectedAmount() != null
                        ? reconciliation.getExpectedAmount().amount() : null)
                .receivedAmount(reconciliation.getReceivedAmount().amount())
                .status(reconciliation.getStatus())
                .reconciledAt(reconciliation.getReconciledAt())
                .notes(reconciliation.getNotes())
                .createdAt(reconciliation.getCreatedAt())
                .build();
    }
}

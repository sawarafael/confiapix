package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankWebhookResponse {

    private String provider;
    private String eventType;
    private boolean processed;
    private boolean imported;
    private boolean skipped;
    private boolean reconciled;
    private String txid;

    public static BankWebhookResponse ignored(String provider, String eventType) {
        return BankWebhookResponse.builder()
                .provider(provider)
                .eventType(eventType)
                .processed(false)
                .imported(false)
                .skipped(false)
                .reconciled(false)
                .build();
    }
}

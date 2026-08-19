package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoneWebhookResponse {

    private String eventType;
    private boolean processed;
    private boolean imported;
    private boolean skipped;
    private boolean reconciled;
    private String txid;

    public static StoneWebhookResponse ignored(String eventType) {
        return StoneWebhookResponse.builder()
                .eventType(eventType)
                .processed(false)
                .imported(false)
                .skipped(false)
                .reconciled(false)
                .build();
    }
}

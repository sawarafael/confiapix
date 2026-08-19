package com.confiapix.application.usecase.bank;

import com.confiapix.application.port.bank.BankProviderRegistry;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.presentation.response.BankWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessBankWebhookUseCase {

    private final BankProviderRegistry providerRegistry;

    @Transactional
    public BankWebhookResponse process(String provider, JsonNode body) {
        BankProviderCodes.normalize(provider);
        return providerRegistry.requireForProvider(provider).processWebhook(body);
    }
}

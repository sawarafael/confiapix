package com.confiapix.infrastructure.integration.bank;

import com.confiapix.application.port.bank.BankIntegrationPlugin;
import com.confiapix.application.port.bank.BankProviderDescriptor;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.IntegrationPluginId;
import com.confiapix.presentation.response.BankConnectionTestResponse;
import com.confiapix.presentation.response.BankSyncResponse;
import com.confiapix.presentation.response.BankWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenericBankPlugin implements BankIntegrationPlugin {

    private static final String RESERVE_MESSAGE =
            "Integração automática em desenvolvimento. Cadastre credenciais para reservar a configuração.";

    @Override
    public IntegrationPluginId pluginId() {
        return IntegrationPluginId.GENERIC;
    }

    @Override
    public BankProviderDescriptor descriptorTemplate() {
        String schemaId = IntegrationCredentialSchemas.GENERIC_OPEN_BANKING;
        return new BankProviderDescriptor(
                null,
                null,
                null,
                "Instituição financeira",
                "Integração PIX via Open Banking",
                true,
                false,
                false,
                false,
                schemaId,
                IntegrationCredentialSchemas.schema(schemaId));
    }

    @Override
    public BankSyncResponse syncRecent(UUID tenantId, int limit) {
        throw new BusinessException(RESERVE_MESSAGE);
    }

    @Override
    public BankConnectionTestResponse testConnection(UUID tenantId) {
        throw new BusinessException(RESERVE_MESSAGE);
    }

    @Override
    public BankWebhookResponse processWebhook(JsonNode body) {
        throw new BusinessException(RESERVE_MESSAGE);
    }
}

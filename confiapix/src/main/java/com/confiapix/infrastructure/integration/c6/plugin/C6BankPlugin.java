package com.confiapix.infrastructure.integration.c6.plugin;

import com.confiapix.application.port.bank.BankIntegrationPlugin;
import com.confiapix.application.port.bank.BankProviderDescriptor;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.IntegrationPluginId;
import com.confiapix.infrastructure.integration.bank.IntegrationCredentialSchemas;
import com.confiapix.presentation.response.BankConnectionTestResponse;
import com.confiapix.presentation.response.BankSyncResponse;
import com.confiapix.presentation.response.BankWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class C6BankPlugin implements BankIntegrationPlugin {

    private static final String NOT_READY =
            "Integração C6 Bank em desenvolvimento. Cadastre credenciais para reservar a configuração.";

    @Override
    public IntegrationPluginId pluginId() {
        return IntegrationPluginId.C6;
    }

    @Override
    public BankProviderDescriptor descriptorTemplate() {
        String schemaId = IntegrationCredentialSchemas.C6_OAUTH;
        return new BankProviderDescriptor(
                BankProviderCodes.C6,
                BankProviderCodes.C6,
                "31872495",
                "Banco C6",
                "Integração C6 Bank — PIX e webhooks (em desenvolvimento)",
                true,
                false,
                true,
                false,
                schemaId,
                IntegrationCredentialSchemas.schema(schemaId));
    }

    @Override
    public BankSyncResponse syncRecent(UUID tenantId, int limit) {
        throw new BusinessException(NOT_READY);
    }

    @Override
    public BankConnectionTestResponse testConnection(UUID tenantId) {
        throw new BusinessException(NOT_READY);
    }

    @Override
    public BankWebhookResponse processWebhook(JsonNode body) {
        throw new BusinessException(NOT_READY);
    }
}

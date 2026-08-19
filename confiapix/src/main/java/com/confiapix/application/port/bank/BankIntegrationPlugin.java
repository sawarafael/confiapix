package com.confiapix.application.port.bank;

import com.confiapix.domain.valueobject.IntegrationPluginId;
import com.confiapix.presentation.response.BankConnectionTestResponse;
import com.confiapix.presentation.response.BankSyncResponse;
import com.confiapix.presentation.response.BankWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public interface BankIntegrationPlugin {

    IntegrationPluginId pluginId();

    BankProviderDescriptor descriptorTemplate();

    BankSyncResponse syncRecent(UUID tenantId, int limit);

    BankConnectionTestResponse testConnection(UUID tenantId);

    BankWebhookResponse processWebhook(JsonNode body);
}

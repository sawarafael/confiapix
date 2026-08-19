package com.confiapix.infrastructure.integration.stone.plugin;

import com.confiapix.application.port.bank.BankIntegrationPlugin;
import com.confiapix.application.port.bank.BankProviderDescriptor;
import com.confiapix.application.usecase.ProcessStoneWebhookUseCase;
import com.confiapix.application.usecase.SyncPixFromStoneUseCase;
import com.confiapix.application.usecase.TestStoneConnectionUseCase;
import com.confiapix.infrastructure.integration.stone.service.StoneWebhookPayloadResolver;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.IntegrationPluginId;
import com.confiapix.infrastructure.integration.bank.IntegrationCredentialSchemas;
import com.confiapix.presentation.response.BankConnectionTestResponse;
import com.confiapix.presentation.response.BankSyncResponse;
import com.confiapix.presentation.response.BankWebhookResponse;
import com.confiapix.presentation.response.StoneConnectionTestResponse;
import com.confiapix.presentation.response.StoneSyncResponse;
import com.confiapix.presentation.response.StoneWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoneBankPlugin implements BankIntegrationPlugin {

    private final SyncPixFromStoneUseCase syncPixFromStoneUseCase;
    private final TestStoneConnectionUseCase testStoneConnectionUseCase;
    private final ProcessStoneWebhookUseCase processStoneWebhookUseCase;
    private final StoneWebhookPayloadResolver webhookPayloadResolver;

    @Override
    public IntegrationPluginId pluginId() {
        return IntegrationPluginId.STONE;
    }

    @Override
    public BankProviderDescriptor descriptorTemplate() {
        String schemaId = IntegrationCredentialSchemas.STONE_API_KEY_OR_OPEN_BANKING;
        return new BankProviderDescriptor(
                BankProviderCodes.STONE,
                null,
                null,
                "Stone",
                "Integração Stone — cobranças, webhook e Open Banking PIX",
                true,
                true,
                true,
                true,
                schemaId,
                IntegrationCredentialSchemas.schema(schemaId));
    }

    @Override
    public BankSyncResponse syncRecent(UUID tenantId, int limit) {
        StoneSyncResponse result = syncPixFromStoneUseCase.syncForTenant(tenantId, limit);
        return BankSyncResponse.builder()
                .provider(BankProviderCodes.STONE)
                .fetched(result.getFetched())
                .imported(result.getImported())
                .reconciled(result.getReconciled())
                .build();
    }

    @Override
    public BankConnectionTestResponse testConnection(UUID tenantId) {
        StoneConnectionTestResponse result = testStoneConnectionUseCase.test();
        return BankConnectionTestResponse.builder()
                .provider(BankProviderCodes.STONE)
                .authMode(result.getAuthMode())
                .success(result.isSuccess())
                .httpStatus(result.getHttpStatus())
                .message(result.getMessage())
                .endpointTested(result.getEndpointTested())
                .hostHeader(result.getHostHeader())
                .responsePreview(result.getResponsePreview())
                .build();
    }

    @Override
    public BankWebhookResponse processWebhook(JsonNode body) {
        StoneWebhookResponse result = processStoneWebhookUseCase.process(webhookPayloadResolver.resolve(body));
        return BankWebhookResponse.builder()
                .provider(BankProviderCodes.STONE)
                .eventType(result.getEventType())
                .processed(result.isProcessed())
                .imported(result.isImported())
                .skipped(result.isSkipped())
                .reconciled(result.isReconciled())
                .txid(result.getTxid())
                .build();
    }
}

package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.bank.BankIntegrationOperationsUseCase;
import com.confiapix.application.usecase.bank.ManageBankIntegrationUseCase;
import com.confiapix.application.usecase.bank.ProcessBankWebhookUseCase;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.bank.BrazilianBankCatalog;
import com.confiapix.presentation.request.BankIntegrationRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.BankConnectionTestResponse;
import com.confiapix.presentation.response.BankIntegrationResponse;
import com.confiapix.presentation.response.BankProviderCatalogItemResponse;
import com.confiapix.presentation.response.BankSyncResponse;
import com.confiapix.presentation.response.BankWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integrations")
@RequiredArgsConstructor
@Tag(name = "Integrações bancárias", description = "Catálogo e credenciais multi-banco")
@SecurityRequirement(name = "bearerAuth")
public class BankIntegrationController {

    private final ManageBankIntegrationUseCase manageBankIntegrationUseCase;
    private final BankIntegrationOperationsUseCase operationsUseCase;
    private final BrazilianBankCatalog bankCatalog;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL')")
    @Operation(summary = "Listar bancos disponíveis e status de ativação do tenant")
    public ApiResponse<List<BankProviderCatalogItemResponse>> listCatalog() {
        return ApiResponse.ok(manageBankIntegrationUseCase.listCatalog());
    }

    @GetMapping("/{provider}/credentials")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar credenciais de um banco")
    public ApiResponse<BankIntegrationResponse> getCredentials(@PathVariable String provider) {
        return ApiResponse.ok(manageBankIntegrationUseCase.get(provider));
    }

    @PutMapping("/{provider}/credentials")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar ou atualizar credenciais de um banco")
    public ApiResponse<BankIntegrationResponse> saveCredentials(
            @PathVariable String provider,
            @Valid @RequestBody BankIntegrationRequest request) {
        String normalized = BankProviderCodes.normalize(provider);
        BankIntegrationResponse response = manageBankIntegrationUseCase.upsert(normalized, request);
        return ApiResponse.ok("Integração " + bankCatalog.displayName(normalized) + " salva com sucesso", response);
    }

    @PostMapping("/{provider}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar integração bancária do tenant")
    public ApiResponse<BankIntegrationResponse> deactivate(@PathVariable String provider) {
        String normalized = BankProviderCodes.normalize(provider);
        return ApiResponse.ok("Integração desativada", manageBankIntegrationUseCase.deactivate(normalized));
    }

    @PostMapping("/{provider}/remove")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover integração bancária do tenant")
    public ApiResponse<Void> removeViaPost(@PathVariable String provider) {
        return remove(provider);
    }

    @DeleteMapping("/{provider}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover integração bancária do tenant")
    public ApiResponse<Void> remove(@PathVariable String provider) {
        String normalized = BankProviderCodes.normalize(provider);
        manageBankIntegrationUseCase.remove(normalized);
        return ApiResponse.ok("Integração removida", null);
    }

    @PostMapping("/{provider}/test-connection")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL')")
    @Operation(summary = "Testar conexão com o banco")
    public ApiResponse<BankConnectionTestResponse> testConnection(@PathVariable String provider) {
        BankConnectionTestResponse result = operationsUseCase.testConnection(provider);
        String message = result.isSuccess() ? "Conexão OK" : "Falha na conexão";
        return ApiResponse.ok(message, result);
    }

    @PostMapping("/{provider}/sync")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL')")
    @Operation(summary = "Sincronizar PIX recentes do banco")
    public ApiResponse<BankSyncResponse> sync(@PathVariable String provider) {
        BankSyncResponse result = operationsUseCase.sync(provider);
        return ApiResponse.ok("Sincronização concluída", result);
    }
}

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks bancários", description = "Notificações externas multi-banco")
class BankWebhookController {

    private final ProcessBankWebhookUseCase processBankWebhookUseCase;

    @PostMapping("/{provider}/pix")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Receber notificação PIX de um banco")
    public ApiResponse<BankWebhookResponse> receivePix(
            @PathVariable String provider,
            @RequestBody JsonNode body) {
        BankWebhookResponse result = processBankWebhookUseCase.process(provider, body);
        return ApiResponse.ok("Webhook processado", result);
    }
}

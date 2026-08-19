package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.SyncPixFromStoneUseCase;
import com.confiapix.application.usecase.TestStoneConnectionUseCase;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.StoneConnectionTestResponse;
import com.confiapix.presentation.response.StoneSyncResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integrations/stone")
@RequiredArgsConstructor
@Tag(name = "Stone", description = "Integração Stone - sincronização")
@SecurityRequirement(name = "bearerAuth")
public class StoneIntegrationController {

    private final SyncPixFromStoneUseCase syncPixFromStoneUseCase;
    private final TestStoneConnectionUseCase testStoneConnectionUseCase;
    private final StoneProperties stoneProperties;

    @PostMapping("/test-connection")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL')")
    @Operation(summary = "Testar credenciais Stone (OAuth ou SecretKey sk_)")
    public ApiResponse<StoneConnectionTestResponse> testConnection() {
        StoneConnectionTestResponse result = testStoneConnectionUseCase.test();
        String message = result.isSuccess() ? "Conexão Stone OK" : "Falha na conexão Stone";
        return ApiResponse.ok(message, result);
    }

    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL')")
    @Operation(summary = "Sincronizar PIX recentes da Stone e conciliar automaticamente")
    public ApiResponse<StoneSyncResponse> sync() {
        StoneSyncResponse result = syncPixFromStoneUseCase.syncRecent(stoneProperties.getDefaultPixPageLimit());
        return ApiResponse.ok("Sincronização Stone concluída", result);
    }
}

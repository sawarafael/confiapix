package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.StoneCredentialsUseCase;
import com.confiapix.presentation.request.StoneCredentialsRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.StoneCredentialsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integrations/stone/credentials")
@RequiredArgsConstructor
@Tag(name = "Stone", description = "Integração Stone - credenciais")
@SecurityRequirement(name = "bearerAuth")
public class StoneCredentialsController {

    private final StoneCredentialsUseCase stoneCredentialsUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar credenciais Stone do tenant")
    public ApiResponse<StoneCredentialsResponse> get() {
        return ApiResponse.ok(stoneCredentialsUseCase.getCurrent());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar ou atualizar credenciais Stone")
    public ApiResponse<StoneCredentialsResponse> upsert(@Valid @RequestBody StoneCredentialsRequest request) {
        return ApiResponse.ok("Credenciais Stone salvas com sucesso", stoneCredentialsUseCase.upsert(request));
    }
}

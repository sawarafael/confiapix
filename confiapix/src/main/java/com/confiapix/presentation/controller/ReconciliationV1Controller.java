package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.ReconciliationQueryUseCase;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.ReconciliationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reconciliations")
@RequiredArgsConstructor
@Tag(name = "Reconciliations", description = "Conciliações PIX x contas a receber")
@SecurityRequirement(name = "bearerAuth")
public class ReconciliationV1Controller {

    private final ReconciliationQueryUseCase reconciliationQueryUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Listar conciliações do tenant")
    public ApiResponse<List<ReconciliationResponse>> list() {
        return ApiResponse.ok(reconciliationQueryUseCase.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Buscar conciliação por ID")
    public ApiResponse<ReconciliationResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(reconciliationQueryUseCase.findById(id));
    }
}

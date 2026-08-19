package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.TenantAccessUseCase;
import com.confiapix.presentation.request.CreateTenantAccessRequest;
import com.confiapix.presentation.request.UpdateTenantAccessRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.TenantAccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/tenant-access")
@RequiredArgsConstructor
@Tag(name = "Tenant Access", description = "Gestão de acessos de empresas clientes (operador da plataforma)")
@SecurityRequirement(name = "bearerAuth")
public class TenantAccessController {

    private final TenantAccessUseCase tenantAccessUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar empresas clientes com acesso ao sistema")
    public ApiResponse<List<TenantAccessResponse>> list() {
        return ApiResponse.ok(tenantAccessUseCase.listCustomerTenants());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Provisionar nova empresa cliente e administrador")
    public ApiResponse<TenantAccessResponse> create(@Valid @RequestBody CreateTenantAccessRequest request) {
        return ApiResponse.ok("Acesso criado com sucesso", tenantAccessUseCase.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar plano ou status de acesso da empresa")
    public ApiResponse<TenantAccessResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantAccessRequest request) {
        return ApiResponse.ok("Acesso atualizado com sucesso", tenantAccessUseCase.update(id, request));
    }
}

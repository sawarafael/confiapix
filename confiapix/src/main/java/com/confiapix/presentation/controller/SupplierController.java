package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.request.SupplierRequest;
import com.confiapix.presentation.response.SupplierResponse;
import com.confiapix.application.usecase.SupplierUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Gestão de fornecedores")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private final SupplierUseCase supplierService;

    @GetMapping
    @Operation(summary = "Listar fornecedores")
    public ApiResponse<PageResponse<SupplierResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(supplierService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor por ID")
    public ApiResponse<SupplierResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(supplierService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar fornecedor")
    public ApiResponse<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ApiResponse.ok("Fornecedor criado com sucesso", supplierService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor")
    public ApiResponse<SupplierResponse> update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        return ApiResponse.ok("Fornecedor atualizado com sucesso", supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir fornecedor")
    public void delete(@PathVariable UUID id) {
        supplierService.delete(id);
    }
}

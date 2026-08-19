package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.request.CompanyRequest;
import com.confiapix.presentation.response.CompanyResponse;
import com.confiapix.application.usecase.CompanyUseCase;
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
@RequestMapping("/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Gestão de empresas")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    private final CompanyUseCase companyService;

    @GetMapping
    @Operation(summary = "Listar empresas")
    public ApiResponse<PageResponse<CompanyResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(companyService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID")
    public ApiResponse<CompanyResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(companyService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar empresa")
    public ApiResponse<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        return ApiResponse.ok("Empresa criada com sucesso", companyService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa")
    public ApiResponse<CompanyResponse> update(@PathVariable UUID id, @Valid @RequestBody CompanyRequest request) {
        return ApiResponse.ok("Empresa atualizada com sucesso", companyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir empresa")
    public void delete(@PathVariable UUID id) {
        companyService.delete(id);
    }
}

package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.ReceivableUseCase;
import com.confiapix.presentation.request.ReceivableRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.response.ReceivableResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receivables")
@RequiredArgsConstructor
@Tag(name = "Receivables V1", description = "Contas a receber — API v1")
@SecurityRequirement(name = "bearerAuth")
public class ReceivableV1Controller {

    private final ReceivableUseCase receivableUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Listar contas a receber")
    public ApiResponse<PageResponse<ReceivableResponse>> list(
            @PageableDefault(size = 20, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(receivableUseCase.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Buscar conta a receber por ID")
    public ApiResponse<ReceivableResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(receivableUseCase.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL')")
    @Operation(summary = "Criar conta a receber")
    public ApiResponse<ReceivableResponse> create(@Valid @RequestBody ReceivableRequest request) {
        return ApiResponse.ok("Conta a receber criada com sucesso", receivableUseCase.create(request));
    }
}

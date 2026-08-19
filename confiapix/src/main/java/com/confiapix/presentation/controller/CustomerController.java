package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.request.CustomerRequest;
import com.confiapix.presentation.response.CustomerResponse;
import com.confiapix.application.usecase.CustomerUseCase;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Gestão de clientes")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerUseCase customerService;

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ApiResponse<PageResponse<CustomerResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(customerService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ApiResponse<CustomerResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(customerService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar cliente")
    public ApiResponse<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok("Cliente criado com sucesso", customerService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    public ApiResponse<CustomerResponse> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok("Cliente atualizado com sucesso", customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir cliente")
    public void delete(@PathVariable UUID id) {
        customerService.delete(id);
    }
}

package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.request.ReceivableRequest;
import com.confiapix.presentation.response.ReceivableResponse;
import com.confiapix.application.usecase.ReceivableUseCase;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/receivables")
@RequiredArgsConstructor
@Tag(name = "Receivables", description = "Gestão de contas a receber")
@SecurityRequirement(name = "bearerAuth")
public class ReceivableController {

    private final ReceivableUseCase receivableService;

    @GetMapping
    @Operation(summary = "Listar contas a receber")
    public ApiResponse<PageResponse<ReceivableResponse>> findAll(
            @PageableDefault(size = 20, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(receivableService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a receber por ID")
    public ApiResponse<ReceivableResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(receivableService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar conta a receber")
    public ApiResponse<ReceivableResponse> create(@Valid @RequestBody ReceivableRequest request) {
        return ApiResponse.ok("Conta a receber criada com sucesso", receivableService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a receber")
    public ApiResponse<ReceivableResponse> update(@PathVariable UUID id, @Valid @RequestBody ReceivableRequest request) {
        return ApiResponse.ok("Conta a receber atualizada com sucesso", receivableService.update(id, request));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Registrar pagamento de conta a receber")
    public ApiResponse<ReceivableResponse> pay(@PathVariable UUID id) {
        return ApiResponse.ok("Pagamento registrado com sucesso", receivableService.pay(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir conta a receber")
    public void delete(@PathVariable UUID id) {
        receivableService.delete(id);
    }
}

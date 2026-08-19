package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.request.PayableRequest;
import com.confiapix.presentation.response.PayableResponse;
import com.confiapix.application.usecase.PayableUseCase;
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
@RequestMapping("/payables")
@RequiredArgsConstructor
@Tag(name = "Payables", description = "Gestão de contas a pagar")
@SecurityRequirement(name = "bearerAuth")
public class PayableController {

    private final PayableUseCase payableService;

    @GetMapping
    @Operation(summary = "Listar contas a pagar")
    public ApiResponse<PageResponse<PayableResponse>> findAll(
            @PageableDefault(size = 20, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(payableService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a pagar por ID")
    public ApiResponse<PayableResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(payableService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar conta a pagar")
    public ApiResponse<PayableResponse> create(@Valid @RequestBody PayableRequest request) {
        return ApiResponse.ok("Conta a pagar criada com sucesso", payableService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a pagar")
    public ApiResponse<PayableResponse> update(@PathVariable UUID id, @Valid @RequestBody PayableRequest request) {
        return ApiResponse.ok("Conta a pagar atualizada com sucesso", payableService.update(id, request));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Registrar pagamento de conta a pagar")
    public ApiResponse<PayableResponse> pay(@PathVariable UUID id) {
        return ApiResponse.ok("Pagamento registrado com sucesso", payableService.pay(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir conta a pagar")
    public void delete(@PathVariable UUID id) {
        payableService.delete(id);
    }
}

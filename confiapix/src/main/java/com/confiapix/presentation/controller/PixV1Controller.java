package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.PixUseCase;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.response.PixDetailResponse;
import com.confiapix.presentation.response.PixResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pix")
@RequiredArgsConstructor
@Tag(name = "PIX", description = "Transações PIX recebidas")
@SecurityRequirement(name = "bearerAuth")
public class PixV1Controller {

    private final PixUseCase pixUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Listar transações PIX do tenant")
    public ApiResponse<PageResponse<PixResponse>> list(
            @PageableDefault(size = 20, sort = "receivedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(pixUseCase.list(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Detalhar transação PIX por ID")
    public ApiResponse<PixDetailResponse> findById(@PathVariable UUID id) {
        return ApiResponse.ok(pixUseCase.findDetailById(id));
    }

    @GetMapping("/txid/{txid}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Detalhar transação PIX por TXID")
    public ApiResponse<PixDetailResponse> findDetailByTxid(@PathVariable String txid) {
        return ApiResponse.ok(pixUseCase.findDetailByTxid(txid));
    }
}

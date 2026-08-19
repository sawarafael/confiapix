package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.DashboardResponse;
import com.confiapix.application.usecase.DashboardUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Indicadores financeiros")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardUseCase dashboardService;

    @GetMapping
    @Operation(summary = "Obter resumo financeiro do tenant")
    public ApiResponse<DashboardResponse> getDashboard() {
        return ApiResponse.ok(dashboardService.getDashboard());
    }
}

package com.confiapix.presentation.controller;

import com.confiapix.presentation.response.AuthResponse;
import com.confiapix.presentation.request.LoginRequest;
import com.confiapix.presentation.request.RefreshTokenRequest;
import com.confiapix.presentation.request.RegisterRequest;
import com.confiapix.application.usecase.AuthUseCase;
import com.confiapix.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e registro")
public class AuthController {

    private final AuthUseCase authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar novo tenant e usuário")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Registro realizado com sucesso", authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token com refresh token")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok("Token renovado com sucesso", authService.refresh(request));
    }
}

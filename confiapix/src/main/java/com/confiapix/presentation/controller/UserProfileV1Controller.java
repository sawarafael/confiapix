package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.UserProfileUseCase;
import com.confiapix.presentation.request.ChangePasswordRequest;
import com.confiapix.presentation.request.UpdateProfileRequest;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.UpdateProfileResponse;
import com.confiapix.presentation.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Perfil", description = "Perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileV1Controller {

    private final UserProfileUseCase userProfileUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Obter perfil do usuário autenticado")
    public ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.ok(userProfileUseCase.getCurrentProfile());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Atualizar nome e e-mail do perfil")
    public ApiResponse<UpdateProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Perfil atualizado com sucesso", userProfileUseCase.updateProfile(request));
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Alterar senha do usuário autenticado")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userProfileUseCase.changePassword(request);
        return ApiResponse.ok("Senha alterada com sucesso", null);
    }
}

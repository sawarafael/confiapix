package com.confiapix.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTenantAccessRequest {

    @NotBlank(message = "Nome da empresa é obrigatório")
    private String tenantName;

    @NotBlank(message = "Plano é obrigatório")
    private String plan;

    @NotBlank(message = "Nome do administrador é obrigatório")
    private String adminName;

    @NotBlank(message = "E-mail do administrador é obrigatório")
    @Email(message = "E-mail inválido")
    private String adminEmail;

    @NotBlank(message = "Senha do administrador é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String adminPassword;
}

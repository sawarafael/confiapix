package com.confiapix.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTenantAccessRequest {

    @NotBlank(message = "Nome da empresa é obrigatório")
    private String tenantName;

    @NotBlank(message = "Plano é obrigatório")
    private String plan;

    private boolean active = true;
}

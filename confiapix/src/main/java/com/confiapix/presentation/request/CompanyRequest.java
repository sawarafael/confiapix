package com.confiapix.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {

    @NotBlank(message = "Razão social é obrigatória")
    private String corporateName;

    @NotBlank(message = "Nome fantasia é obrigatório")
    private String tradeName;

    @NotBlank(message = "Documento é obrigatório")
    private String document;

    private boolean active = true;
}

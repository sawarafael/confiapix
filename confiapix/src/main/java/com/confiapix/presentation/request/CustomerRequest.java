package com.confiapix.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String document;

    @Email(message = "E-mail inválido")
    private String email;

    private String phone;

    @NotNull(message = "Empresa é obrigatória")
    private UUID companyId;
}

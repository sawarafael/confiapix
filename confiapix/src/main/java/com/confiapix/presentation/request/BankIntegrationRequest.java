package com.confiapix.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BankIntegrationRequest {

    private String clientId;

    @NotBlank(message = "accountRef é obrigatório")
    private String accountRef;

    private String clientSecret;

    private String merchantRef;

    private Map<String, String> config;

    private Boolean active;
}

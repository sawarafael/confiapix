package com.confiapix.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoneCredentialsRequest {

    /**
     * OPEN_BANKING (OAuth client_id + client_secret) ou API_KEY (SecretKey sk_...).
     */
    private String authMode;

    /**
     * GATEWAY ou SUBACQUIRER — define o header Host no modo API_KEY (Stone Online).
     */
    private String businessModel;

    /** Obrigatório no modo OPEN_BANKING. No modo API_KEY pode ser omitido. */
    private String clientId;

    /** client_secret (Open Banking) ou SecretKey sk_... (API_KEY). */
    @NotBlank(message = "client_secret / SecretKey é obrigatório")
    private String clientSecret;

    @NotBlank(message = "account_id é obrigatório")
    private String accountId;

    private String merchantId;
}

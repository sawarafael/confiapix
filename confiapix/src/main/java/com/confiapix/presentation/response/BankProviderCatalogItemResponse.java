package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class BankProviderCatalogItemResponse {

    private String provider;
    private String compe;
    private String ispb;
    private String displayName;
    private String description;
    private boolean available;
    private boolean configured;
    private boolean active;
    private boolean supportsSync;
    private boolean supportsWebhook;
    private boolean supportsConnectionTest;
    private String credentialSchemaId;
    private Map<String, Object> credentialSchema;
}

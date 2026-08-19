package com.confiapix.application.port.bank;

import java.util.Map;

public record BankProviderDescriptor(
        String provider,
        String compe,
        String ispb,
        String displayName,
        String description,
        boolean available,
        boolean supportsSync,
        boolean supportsWebhook,
        boolean supportsConnectionTest,
        String credentialSchemaId,
        Map<String, Object> credentialSchema) {
}

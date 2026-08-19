package com.confiapix.domain.valueobject;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum BankProvider {
    STONE("Stone", "Integração Stone — cobranças, webhook e Open Banking PIX"),
    INTER("Banco Inter", "Integração Banco Inter — PIX e webhooks (em desenvolvimento)"),
    C6("C6 Bank", "Integração C6 Bank — PIX e webhooks (em desenvolvimento)");

    private final String displayName;
    private final String description;

    BankProvider(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public static BankProvider from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Provedor bancário é obrigatório");
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(provider -> provider.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provedor bancário inválido: " + raw));
    }

    public static Optional<BankProvider> tryParse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(from(raw));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

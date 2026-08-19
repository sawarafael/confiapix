package com.confiapix.domain.valueobject;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Codigo de instituicao financeira para integracao.
 * Usa COMPE (3 digitos) para bancos BACEN ou {@code STONE} para gateway Stone.
 */
public final class BankProviderCodes {

    public static final String STONE = "STONE";
    public static final String INTER = "077";
    public static final String C6 = "336";

    private static final Map<String, String> LEGACY_ALIASES = Map.of(
            "INTER", INTER,
            "C6", C6);

    private static final Set<String> KNOWN_CODES = Set.of(STONE);

    private BankProviderCodes() {
    }

    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Provedor bancário é obrigatório");
        }
        String trimmed = raw.trim().toUpperCase(Locale.ROOT);
        if (LEGACY_ALIASES.containsKey(trimmed)) {
            return LEGACY_ALIASES.get(trimmed);
        }
        if (STONE.equals(trimmed)) {
            return STONE;
        }
        if (trimmed.matches("\\d{1,3}")) {
            return String.format("%03d", Integer.parseInt(trimmed));
        }
        throw new IllegalArgumentException("Provedor bancário inválido: " + raw);
    }

    public static IntegrationPluginId pluginFor(String providerCode) {
        String normalized = normalize(providerCode);
        return switch (normalized) {
            case STONE -> IntegrationPluginId.STONE;
            case INTER -> IntegrationPluginId.INTER;
            case C6 -> IntegrationPluginId.C6;
            default -> IntegrationPluginId.GENERIC;
        };
    }

    public static boolean isKnownCatalogCode(String providerCode) {
        if (providerCode == null || providerCode.isBlank()) {
            return false;
        }
        try {
            String normalized = normalize(providerCode);
            return KNOWN_CODES.contains(normalized) || normalized.matches("\\d{3}");
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}

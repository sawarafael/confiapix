package com.confiapix.infrastructure.integration.bank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BrazilianBankCatalog {

    private final ObjectMapper objectMapper;

    @Getter
    private List<BrazilianBankInstitution> institutions = List.of();

    private Map<String, BrazilianBankInstitution> byCompe = Map.of();

    public BrazilianBankCatalog(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void load() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/brazilian-banks.json");
        institutions = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        institutions = institutions.stream()
                .sorted(Comparator.comparing(BrazilianBankInstitution::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        byCompe = institutions.stream()
                .collect(Collectors.toUnmodifiableMap(
                        bank -> normalizeCompe(bank.getCompe()),
                        Function.identity(),
                        (left, right) -> left));
    }

    public Optional<BrazilianBankInstitution> findByCompe(String compe) {
        if (compe == null || compe.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(byCompe.get(normalizeCompe(compe)));
    }

    public String displayName(String providerCode) {
        if ("STONE".equalsIgnoreCase(providerCode)) {
            return "Stone";
        }
        return findByCompe(providerCode)
                .map(BrazilianBankInstitution::getName)
                .orElse("Instituição " + providerCode);
    }

    public static String normalizeCompe(String compe) {
        if (compe == null || compe.isBlank()) {
            return compe;
        }
        String trimmed = compe.trim();
        if ("STONE".equalsIgnoreCase(trimmed)) {
            return "STONE";
        }
        if (trimmed.matches("\\d{1,3}")) {
            return String.format("%03d", Integer.parseInt(trimmed));
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }
}

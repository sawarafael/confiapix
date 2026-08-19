package com.confiapix.infrastructure.integration.bank;

import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BankIntegrationConfigSupport {

    private final ObjectMapper objectMapper;

    public StoneAuthMode readStoneAuthMode(BankIntegrationJpaEntity entity) {
        Object raw = readConfig(entity).get("authMode");
        if (raw instanceof String value && !value.isBlank()) {
            return StoneAuthMode.valueOf(value.trim().toUpperCase());
        }
        return StoneAuthMode.OPEN_BANKING;
    }

    public StoneBusinessModel readStoneBusinessModel(BankIntegrationJpaEntity entity) {
        Object raw = readConfig(entity).get("businessModel");
        if (raw instanceof String value && !value.isBlank()) {
            return StoneBusinessModel.valueOf(value.trim().toUpperCase());
        }
        return StoneBusinessModel.GATEWAY;
    }

    public String writeStoneConfig(StoneAuthMode authMode, StoneBusinessModel businessModel) {
        Map<String, String> config = new LinkedHashMap<>();
        config.put("authMode", authMode.name());
        config.put("businessModel", businessModel.name());
        return writeConfig(config);
    }

    public String writeGenericConfig(Map<String, String> config) {
        return writeConfig(config != null ? config : Map.of());
    }

    public Map<String, String> readConfig(BankIntegrationJpaEntity entity) {
        if (entity.getConfigJson() == null || entity.getConfigJson().isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(entity.getConfigJson(), new TypeReference<>() {
            });
        } catch (Exception ex) {
            return Map.of();
        }
    }

    public BankIntegrationJpaEntity newEntity(UUID tenantId, String provider) {
        return BankIntegrationJpaEntity.builder()
                .tenantId(tenantId)
                .provider(BankProviderCodes.normalize(provider))
                .active(true)
                .configJson(writeConfig(Map.of()))
                .build();
    }

    private String writeConfig(Map<String, String> config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao serializar config_json", ex);
        }
    }
}

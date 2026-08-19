package com.confiapix.infrastructure.integration.stone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoneWebhookCounterParty(StonePixEntity entity) {
}

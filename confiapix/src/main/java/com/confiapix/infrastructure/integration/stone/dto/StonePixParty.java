package com.confiapix.infrastructure.integration.stone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StonePixParty(StonePixEntity entity) {
}

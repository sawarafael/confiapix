package com.confiapix.infrastructure.integration.stone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StoneTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType) {
}

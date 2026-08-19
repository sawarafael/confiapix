package com.confiapix.infrastructure.integration.stone.model;

import java.time.Instant;

public record StoneAccessToken(String value, Instant expiresAt) {

    public boolean isValid(Instant now, long bufferSeconds) {
        return expiresAt.isAfter(now.plusSeconds(bufferSeconds));
    }
}

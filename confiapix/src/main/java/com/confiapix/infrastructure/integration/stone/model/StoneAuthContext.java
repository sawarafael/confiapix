package com.confiapix.infrastructure.integration.stone.model;

import java.util.Map;

public record StoneAuthContext(
        String authorizationHeader,
        Map<String, String> extraHeaders,
        StoneAuthMode authMode) {
}

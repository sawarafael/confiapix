package com.confiapix.infrastructure.integration.stone.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class StoneBasicAuthSupport {

    private StoneBasicAuthSupport() {
    }

    public static String toBasicAuthorization(String secretKey) {
        String token = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }
}

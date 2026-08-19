package com.confiapix.infrastructure.integration.stone.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoneBasicAuthSupportTest {

    @Test
    void shouldBuildBasicAuthorizationHeader() {
        String header = StoneBasicAuthSupport.toBasicAuthorization("sk_test_key");
        assertThat(header).startsWith("Basic ");
        assertThat(header).isEqualTo("Basic " + java.util.Base64.getEncoder()
                .encodeToString("sk_test_key:".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }
}

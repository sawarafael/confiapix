package com.confiapix.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecretEncryptionServiceTest {

    private SecretEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new SecretEncryptionService("test-encryption-secret-for-unit-tests-32chars-minimum");
    }

    @Test
    void shouldEncryptAndDecryptSecret() {
        String plain = "stone-client-secret-value";

        String encrypted = encryptionService.encrypt(plain);
        String decrypted = encryptionService.decrypt(encrypted);

        assertThat(encrypted).isNotEqualTo(plain);
        assertThat(decrypted).isEqualTo(plain);
    }
}

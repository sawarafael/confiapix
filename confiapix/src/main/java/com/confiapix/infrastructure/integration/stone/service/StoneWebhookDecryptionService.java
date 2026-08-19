package com.confiapix.infrastructure.integration.stone.service;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.crypto.RsaKeyLoader;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoneWebhookDecryptionService {

    private final StoneProperties stoneProperties;
    private final ObjectMapper objectMapper;

    public boolean isConfigured() {
        String privateKey = stoneProperties.getWebhookPrivateKeyPem();
        return privateKey != null && !privateKey.isBlank();
    }

    public StoneWebhookPayload decrypt(String encryptedBody) {
        if (!isConfigured()) {
            throw new BusinessException("Webhook cifrado requer configuração de chave privada RSA");
        }
        try {
            JWEObject jwe = JWEObject.parse(encryptedBody);
            RSAPrivateKey privateKey = RsaKeyLoader.loadPrivateKey(stoneProperties.getWebhookPrivateKeyPem());
            jwe.decrypt(new RSADecrypter(privateKey));

            SignedJWT signedJwt = SignedJWT.parse(jwe.getPayload().toString());
            verifySignatureIfConfigured(signedJwt);

            StoneWebhookPayload payload = objectMapper.convertValue(
                    signedJwt.getJWTClaimsSet().toJSONObject(), StoneWebhookPayload.class);
            log.info("Webhook Stone decifrado eventType={}", payload.eventType());
            return payload;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("Falha ao decifrar webhook Stone: {}", ex.getMessage());
            throw new BusinessException("Falha ao decifrar webhook Stone");
        }
    }

    private void verifySignatureIfConfigured(SignedJWT signedJwt) throws Exception {
        String publicKeyPem = stoneProperties.getWebhookPublicKeyPem();
        if (publicKeyPem == null || publicKeyPem.isBlank()) {
            return;
        }
        RSAPublicKey publicKey = RsaKeyLoader.loadPublicKey(publicKeyPem);
        if (!signedJwt.verify(new RSASSAVerifier(publicKey))) {
            throw new BusinessException("Assinatura do webhook Stone inválida");
        }
    }
}

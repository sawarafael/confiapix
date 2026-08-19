package com.confiapix.support;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

public final class StoneWebhookJweTestHelper {

    private StoneWebhookJweTestHelper() {
    }

    public record KeyMaterial(String privateKeyPem, String publicKeyPem, RSAPrivateKey privateKey, RSAPublicKey publicKey) {
    }

    public static KeyMaterial generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return new KeyMaterial(toPem("PRIVATE KEY", privateKey.getEncoded()),
                toPem("PUBLIC KEY", publicKey.getEncoded()), privateKey, publicKey);
    }

    public static String encryptWebhook(KeyMaterial merchantKeys, KeyMaterial stoneSignerKeys, Map<String, Object> claims)
            throws Exception {
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder();
        claims.forEach(claimsBuilder::claim);
        JWTClaimsSet jwtClaims = claimsBuilder.build();

        SignedJWT signedJwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), jwtClaims);
        signedJwt.sign(new RSASSASigner(stoneSignerKeys.privateKey()));

        JWEObject jwe = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM).build(),
                new Payload(signedJwt.serialize()));
        jwe.encrypt(new RSAEncrypter(merchantKeys.publicKey()));
        return jwe.serialize();
    }

    private static String toPem(String type, byte[] encoded) {
        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----";
    }
}

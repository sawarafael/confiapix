package com.confiapix.infrastructure.integration.stone.crypto;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaKeyLoader {

    private RsaKeyLoader() {
    }

    public static RSAPrivateKey loadPrivateKey(String pem) {
        try {
            byte[] decoded = decodePem(pem, "PRIVATE KEY", "RSA PRIVATE KEY");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Chave privada RSA inválida", ex);
        }
    }

    public static RSAPublicKey loadPublicKey(String pem) {
        try {
            byte[] decoded = decodePem(pem, "PUBLIC KEY", "RSA PUBLIC KEY");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Chave pública RSA inválida", ex);
        }
    }

    private static byte[] decodePem(String pem, String... markers) {
        String normalized = pem
                .replace("-----BEGIN " + markers[0] + "-----", "")
                .replace("-----END " + markers[0] + "-----", "");
        if (markers.length > 1) {
            normalized = normalized
                    .replace("-----BEGIN " + markers[1] + "-----", "")
                    .replace("-----END " + markers[1] + "-----", "");
        }
        return Base64.getMimeDecoder().decode(normalized.replaceAll("\\s", ""));
    }
}

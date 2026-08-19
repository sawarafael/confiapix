package com.confiapix.infrastructure.security;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.confiapix.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Value("${confiapix.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Transactional
    public String issueRefreshToken(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);

        String rawToken = generateRawToken();
        RefreshTokenJpaEntity entity = RefreshTokenJpaEntity.builder()
                .userId(userId)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        refreshTokenRepository.save(entity);
        return rawToken;
    }

    @Transactional
    public UUID validateAndConsume(String rawToken) {
        RefreshTokenJpaEntity entity = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hashToken(rawToken))
                .orElseThrow(() -> new BusinessException("Refresh token inválido"));

        if (entity.getExpiresAt().isBefore(Instant.now())) {
            entity.setRevoked(true);
            refreshTokenRepository.save(entity);
            throw new BusinessException("Refresh token expirado");
        }

        entity.setRevoked(true);
        refreshTokenRepository.save(entity);
        return entity.getUserId();
    }

    private String generateRawToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }
}

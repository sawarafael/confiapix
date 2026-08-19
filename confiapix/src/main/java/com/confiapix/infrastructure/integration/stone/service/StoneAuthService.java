package com.confiapix.infrastructure.integration.stone.service;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.bank.BankIntegrationConfigSupport;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.dto.StoneTokenResponse;
import com.confiapix.infrastructure.integration.stone.model.StoneAccessToken;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthContext;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.infrastructure.security.SecretEncryptionService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class StoneAuthService {

    private final BankIntegrationJpaRepository integrationRepository;
    private final BankIntegrationConfigSupport configSupport;
    private final SecretEncryptionService encryptionService;
    private final StoneProperties stoneProperties;
    private final RestClient tokenRestClient;
    private final Cache<UUID, StoneAccessToken> tokenCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .build();

    public StoneAuthService(
            BankIntegrationJpaRepository integrationRepository,
            BankIntegrationConfigSupport configSupport,
            SecretEncryptionService encryptionService,
            StoneProperties stoneProperties,
            RestClient.Builder restClientBuilder) {
        this.integrationRepository = integrationRepository;
        this.configSupport = configSupport;
        this.encryptionService = encryptionService;
        this.stoneProperties = stoneProperties;
        this.tokenRestClient = restClientBuilder.build();
    }

    public StoneAuthContext resolveAuth(UUID tenantId) {
        BankIntegrationJpaEntity credentials = requireStoneCredentials(tenantId);
        StoneAuthMode authMode = configSupport.readStoneAuthMode(credentials);

        if (authMode == StoneAuthMode.API_KEY) {
            return buildApiKeyAuth(credentials);
        }
        return new StoneAuthContext(
                "Bearer " + getAccessToken(tenantId),
                buildOpenBankingHeaders(credentials),
                StoneAuthMode.OPEN_BANKING);
    }

    public String getAccessToken(UUID tenantId) {
        BankIntegrationJpaEntity credentials = requireStoneCredentials(tenantId);
        if (configSupport.readStoneAuthMode(credentials) == StoneAuthMode.API_KEY) {
            throw new BusinessException("Modo API_KEY não utiliza token OAuth; use resolveAuth()");
        }

        StoneAccessToken cached = tokenCache.getIfPresent(tenantId);
        Instant now = Instant.now();
        if (cached != null && cached.isValid(now, stoneProperties.getTokenRefreshBufferSeconds())) {
            return cached.value();
        }

        StoneAccessToken refreshed = requestNewToken(credentials);
        tokenCache.put(tenantId, refreshed);
        return refreshed.value();
    }

    public void evictToken(UUID tenantId) {
        tokenCache.invalidate(tenantId);
    }

    private StoneAuthContext buildApiKeyAuth(BankIntegrationJpaEntity credentials) {
        String secretKey = encryptionService.decrypt(credentials.getClientSecretEncrypted());
        if (!secretKey.startsWith("sk_")) {
            log.warn("SecretKey Stone API_KEY sem prefixo sk_ — verifique se a chave está correta");
        }

        Map<String, String> headers = new LinkedHashMap<>();
        StoneBusinessModel businessModel = configSupport.readStoneBusinessModel(credentials);
        headers.put("Host", stoneProperties.resolvePaymentsHost(businessModel));

        if (credentials.getAccountRef() != null && !credentials.getAccountRef().isBlank()) {
            headers.put("x-stone-account-id", credentials.getAccountRef().trim());
        }

        return new StoneAuthContext(
                StoneBasicAuthSupport.toBasicAuthorization(secretKey),
                headers,
                StoneAuthMode.API_KEY);
    }

    private Map<String, String> buildOpenBankingHeaders(BankIntegrationJpaEntity credentials) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (credentials.getAccountRef() != null && !credentials.getAccountRef().isBlank()) {
            headers.put("x-stone-account-id", credentials.getAccountRef().trim());
        }
        return headers;
    }

    private StoneAccessToken requestNewToken(BankIntegrationJpaEntity credentials) {
        if (credentials.getClientId() == null || credentials.getClientId().isBlank()) {
            throw new BusinessException("client_id é obrigatório no modo OPEN_BANKING");
        }

        String clientSecret = encryptionService.decrypt(credentials.getClientSecretEncrypted());

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", credentials.getClientId());
        form.add("client_secret", clientSecret);

        try {
            StoneTokenResponse response = tokenRestClient.post()
                    .uri(stoneProperties.getTokenUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(StoneTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new BusinessException("Resposta inválida ao obter token Stone");
            }

            Instant expiresAt = Instant.now().plusSeconds(
                    Math.max(response.expiresIn() - stoneProperties.getTokenRefreshBufferSeconds(), 30));
            log.debug("Token Stone obtido para tenant {} expira em {}", credentials.getTenantId(), expiresAt);
            return new StoneAccessToken(response.accessToken(), expiresAt);
        } catch (RestClientResponseException ex) {
            log.error("Falha OAuth Stone tenant {} status={}", credentials.getTenantId(), ex.getStatusCode().value());
            throw new BusinessException("Falha na autenticação Stone: " + ex.getStatusCode().value());
        }
    }

    private BankIntegrationJpaEntity requireStoneCredentials(UUID tenantId) {
        return integrationRepository.findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE)
                .orElseThrow(() -> new BusinessException("Credenciais Stone não configuradas para o tenant"));
    }
}

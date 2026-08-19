package com.confiapix.infrastructure.integration.stone.client;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.dto.StonePixListResponse;
import com.confiapix.infrastructure.integration.stone.dto.StonePixPaymentItem;
import com.confiapix.infrastructure.integration.stone.mapper.StonePixMapper;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthContext;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.service.StoneAuthService;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class StonePixClient {

    private final StoneAuthService stoneAuthService;
    private final BankIntegrationJpaRepository integrationRepository;
    private final StoneProperties stoneProperties;
    private final RestClient stoneRestClient;
    private final ObjectMapper objectMapper;

    public StonePixClient(
            StoneAuthService stoneAuthService,
            BankIntegrationJpaRepository integrationRepository,
            StoneProperties stoneProperties,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper) {
        this.stoneAuthService = stoneAuthService;
        this.integrationRepository = integrationRepository;
        this.stoneProperties = stoneProperties;
        this.stoneRestClient = restClientBuilder.baseUrl(stoneProperties.getBaseUrl()).build();
        this.objectMapper = objectMapper;
    }

    public Optional<PixTransaction> findPixByTxid(UUID tenantId, String txid) {
        StonePixListResponse response = fetchPixPayments(tenantId, builder -> builder
                .queryParam("transaction_ids", txid)
                .queryParam("limit", 1));

        return response.data().stream()
                .filter(StonePixPaymentItem::isInboundReceived)
                .findFirst()
                .map(item -> StonePixMapper.toDomain(tenantId, item, objectMapper));
    }

    public List<PixTransaction> findRecentPix(UUID tenantId) {
        return findRecentPix(tenantId, stoneProperties.getDefaultPixPageLimit());
    }

    public List<PixTransaction> findRecentPix(UUID tenantId, int limit) {
        StonePixListResponse response = fetchPixPayments(tenantId, builder -> builder
                .queryParam("limit", limit));

        return response.data().stream()
                .filter(StonePixPaymentItem::isInboundReceived)
                .map(item -> StonePixMapper.toDomain(tenantId, item, objectMapper))
                .toList();
    }

    private StonePixListResponse fetchPixPayments(UUID tenantId, java.util.function.Consumer<UriComponentsBuilder> queryCustomizer) {
        BankIntegrationJpaEntity credentials = integrationRepository
                .findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE)
                .orElseThrow(() -> new BusinessException("Credenciais Stone não configuradas para o tenant"));

        String accountId = resolveAccountRef(credentials);
        StoneAuthContext auth = stoneAuthService.resolveAuth(tenantId);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromPath("/api/v1/pix/{accountId}/pix_payments");
        queryCustomizer.accept(uriBuilder);

        try {
            var requestSpec = stoneRestClient.get()
                    .uri(uriBuilder.buildAndExpand(accountId).toUri())
                    .header("Authorization", auth.authorizationHeader());
            auth.extraHeaders().forEach(requestSpec::header);

            StonePixListResponse response = requestSpec.retrieve().body(StonePixListResponse.class);

            if (response == null || response.data() == null) {
                return new StonePixListResponse(List.of());
            }
            return response;
        } catch (RestClientResponseException ex) {
            log.error("Falha consulta PIX Stone tenant {} status={} authMode={}",
                    tenantId, ex.getStatusCode().value(), auth.authMode());
            if (auth.authMode() == StoneAuthMode.API_KEY) {
                throw new BusinessException(
                        "Falha ao consultar PIX Open Banking com API_KEY (HTTP "
                                + ex.getStatusCode().value()
                                + "). Use POST /api/v1/integrations/stone/test-connection para validar a SecretKey. "
                                + "PIX via Open Banking pode exigir credenciais OAuth (modo OPEN_BANKING).");
            }
            throw new BusinessException("Falha ao consultar PIX na Stone: " + ex.getStatusCode().value());
        }
    }

    private String resolveAccountRef(BankIntegrationJpaEntity credentials) {
        if (credentials.getAccountRef() != null && !credentials.getAccountRef().isBlank()) {
            return credentials.getAccountRef();
        }
        if (credentials.getMerchantRef() != null && !credentials.getMerchantRef().isBlank()) {
            return credentials.getMerchantRef();
        }
        throw new BusinessException("account_ref Stone não configurado");
    }
}

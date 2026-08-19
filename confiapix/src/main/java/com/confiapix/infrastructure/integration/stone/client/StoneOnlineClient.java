package com.confiapix.infrastructure.integration.stone.client;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.bank.BankIntegrationConfigSupport;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthContext;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.service.StoneAuthService;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Slf4j
@Component
public class StoneOnlineClient {

    private final StoneAuthService stoneAuthService;
    private final BankIntegrationJpaRepository integrationRepository;
    private final BankIntegrationConfigSupport configSupport;
    private final StoneProperties stoneProperties;
    private final RestClient paymentsRestClient;

    public StoneOnlineClient(
            StoneAuthService stoneAuthService,
            BankIntegrationJpaRepository integrationRepository,
            BankIntegrationConfigSupport configSupport,
            StoneProperties stoneProperties,
            RestClient.Builder restClientBuilder) {
        this.stoneAuthService = stoneAuthService;
        this.integrationRepository = integrationRepository;
        this.configSupport = configSupport;
        this.stoneProperties = stoneProperties;
        this.paymentsRestClient = restClientBuilder.baseUrl(stoneProperties.getPaymentsBaseUrl()).build();
    }

    public StoneConnectionResult testConnection(UUID tenantId) {
        BankIntegrationJpaEntity credentials = integrationRepository
                .findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE)
                .orElseThrow(() -> new BusinessException("Credenciais Stone não configuradas para o tenant"));

        if (configSupport.readStoneAuthMode(credentials) != StoneAuthMode.API_KEY) {
            throw new BusinessException("Teste Stone Online disponível apenas no modo API_KEY");
        }

        StoneAuthContext auth = stoneAuthService.resolveAuth(tenantId);
        String endpoint = stoneProperties.getPaymentsBaseUrl() + "/v1/charges?limit=1";

        try {
            var spec = paymentsRestClient.get()
                    .uri("/v1/charges?limit=1")
                    .header("Authorization", auth.authorizationHeader());
            auth.extraHeaders().forEach(spec::header);

            String body = spec.retrieve().body(String.class);
            return StoneConnectionResult.success(endpoint, auth.extraHeaders().get("Host"), body);
        } catch (RestClientResponseException ex) {
            log.warn("Teste Stone Online tenant {} status={}", tenantId, ex.getStatusCode().value());
            return StoneConnectionResult.failure(
                    endpoint,
                    auth.extraHeaders().get("Host"),
                    ex.getStatusCode().value(),
                    ex.getResponseBodyAsString());
        }
    }

    public record StoneConnectionResult(
            boolean success,
            String endpoint,
            String hostHeader,
            int httpStatus,
            String responsePreview,
            String message) {

        public static StoneConnectionResult success(String endpoint, String hostHeader, String body) {
            return new StoneConnectionResult(
                    true,
                    endpoint,
                    hostHeader,
                    200,
                    truncate(body),
                    "Conexão Stone Online OK");
        }

        public static StoneConnectionResult failure(String endpoint, String hostHeader, int status, String body) {
            return new StoneConnectionResult(
                    false,
                    endpoint,
                    hostHeader,
                    status,
                    truncate(body),
                    "Falha na conexão Stone Online: HTTP " + status);
        }

        private static String truncate(String body) {
            if (body == null) {
                return null;
            }
            return body.length() > 500 ? body.substring(0, 500) + "..." : body;
        }
    }
}

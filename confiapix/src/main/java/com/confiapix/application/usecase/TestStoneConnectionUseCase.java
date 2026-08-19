package com.confiapix.application.usecase;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.bank.BankIntegrationConfigSupport;
import com.confiapix.infrastructure.integration.stone.client.StoneOnlineClient;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.service.StoneAuthService;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.StoneConnectionTestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestStoneConnectionUseCase {

    private final BankIntegrationJpaRepository integrationRepository;
    private final BankIntegrationConfigSupport configSupport;
    private final StoneAuthService stoneAuthService;
    private final StoneOnlineClient stoneOnlineClient;

    @Transactional(readOnly = true)
    public StoneConnectionTestResponse test() {
        UUID tenantId = TenantContextHolder.getTenantId();
        BankIntegrationJpaEntity credentials = integrationRepository
                .findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE)
                .orElseThrow(() -> new BusinessException("Credenciais Stone não configuradas"));

        if (configSupport.readStoneAuthMode(credentials) == StoneAuthMode.API_KEY) {
            StoneOnlineClient.StoneConnectionResult result = stoneOnlineClient.testConnection(tenantId);
            return StoneConnectionTestResponse.builder()
                    .authMode(StoneAuthMode.API_KEY.name())
                    .success(result.success())
                    .httpStatus(result.httpStatus())
                    .message(result.message())
                    .endpointTested(result.endpoint())
                    .hostHeader(result.hostHeader())
                    .responsePreview(result.responsePreview())
                    .build();
        }

        try {
            stoneAuthService.getAccessToken(tenantId);
            return StoneConnectionTestResponse.builder()
                    .authMode(StoneAuthMode.OPEN_BANKING.name())
                    .success(true)
                    .httpStatus(200)
                    .message("Token OAuth Stone obtido com sucesso")
                    .endpointTested("OAuth token endpoint")
                    .build();
        } catch (BusinessException ex) {
            return StoneConnectionTestResponse.builder()
                    .authMode(StoneAuthMode.OPEN_BANKING.name())
                    .success(false)
                    .httpStatus(401)
                    .message(ex.getMessage())
                    .endpointTested("OAuth token endpoint")
                    .build();
        }
    }
}

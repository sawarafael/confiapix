package com.confiapix.application.usecase;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.bank.BankIntegrationConfigSupport;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import com.confiapix.infrastructure.integration.stone.service.StoneAuthService;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.infrastructure.security.SecretEncryptionService;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.request.StoneCredentialsRequest;
import com.confiapix.presentation.response.StoneCredentialsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoneCredentialsUseCase {

    private final BankIntegrationJpaRepository integrationRepository;
    private final SecretEncryptionService encryptionService;
    private final StoneAuthService stoneAuthService;
    private final BankIntegrationConfigSupport configSupport;

    @Transactional(readOnly = true)
    public StoneCredentialsResponse getCurrent() {
        UUID tenantId = TenantContextHolder.getTenantId();
        BankIntegrationJpaEntity entity = integrationRepository.findByTenantIdAndProvider(tenantId, BankProviderCodes.STONE)
                .orElseThrow(() -> new BusinessException("Credenciais Stone não configuradas"));
        return toResponse(entity);
    }

    @Transactional
    public StoneCredentialsResponse upsert(StoneCredentialsRequest request) {
        UUID tenantId = TenantContextHolder.getTenantId();
        StoneAuthMode authMode = resolveAuthMode(request.getAuthMode());
        StoneBusinessModel businessModel = resolveBusinessModel(request.getBusinessModel());
        validateRequest(request, authMode);

        BankIntegrationJpaEntity entity = integrationRepository.findByTenantIdAndProvider(tenantId, BankProviderCodes.STONE)
                .orElseGet(() -> configSupport.newEntity(tenantId, BankProviderCodes.STONE));

        entity.setClientId(resolveClientId(request, authMode));
        entity.setClientSecretEncrypted(encryptionService.encrypt(request.getClientSecret().trim()));
        entity.setAccountRef(request.getAccountId().trim());
        entity.setMerchantRef(request.getMerchantId() != null ? request.getMerchantId().trim() : null);
        entity.setConfigJson(configSupport.writeStoneConfig(authMode, businessModel));
        entity.setActive(true);

        BankIntegrationJpaEntity saved = integrationRepository.save(entity);
        stoneAuthService.evictToken(tenantId);
        return toResponse(saved);
    }

    private void validateRequest(StoneCredentialsRequest request, StoneAuthMode authMode) {
        if (authMode == StoneAuthMode.OPEN_BANKING) {
            if (request.getClientId() == null || request.getClientId().isBlank()) {
                throw new BusinessException("client_id é obrigatório no modo OPEN_BANKING");
            }
            return;
        }

        if (!request.getClientSecret().trim().startsWith("sk_")) {
            throw new BusinessException("No modo API_KEY, informe a SecretKey Stone (prefixo sk_) em clientSecret");
        }
    }

    private String resolveClientId(StoneCredentialsRequest request, StoneAuthMode authMode) {
        if (request.getClientId() != null && !request.getClientId().isBlank()) {
            return request.getClientId().trim();
        }
        if (authMode == StoneAuthMode.API_KEY) {
            return request.getAccountId().trim();
        }
        throw new BusinessException("client_id é obrigatório");
    }

    private StoneAuthMode resolveAuthMode(String raw) {
        if (raw == null || raw.isBlank()) {
            return StoneAuthMode.OPEN_BANKING;
        }
        try {
            return StoneAuthMode.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("authMode inválido. Use OPEN_BANKING ou API_KEY");
        }
    }

    private StoneBusinessModel resolveBusinessModel(String raw) {
        if (raw == null || raw.isBlank()) {
            return StoneBusinessModel.GATEWAY;
        }
        try {
            return StoneBusinessModel.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("businessModel inválido. Use GATEWAY ou SUBACQUIRER");
        }
    }

    private StoneCredentialsResponse toResponse(BankIntegrationJpaEntity entity) {
        return StoneCredentialsResponse.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .authMode(configSupport.readStoneAuthMode(entity).name())
                .businessModel(configSupport.readStoneBusinessModel(entity).name())
                .clientId(entity.getClientId())
                .accountId(entity.getAccountRef())
                .merchantId(entity.getMerchantRef())
                .active(entity.isActive())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

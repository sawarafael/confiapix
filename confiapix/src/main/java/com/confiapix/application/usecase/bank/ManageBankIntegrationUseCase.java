package com.confiapix.application.usecase.bank;

import com.confiapix.application.port.bank.BankProviderRegistry;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.IntegrationPluginId;
import com.confiapix.infrastructure.integration.bank.BrazilianBankCatalog;
import com.confiapix.infrastructure.integration.bank.BankIntegrationConfigSupport;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import com.confiapix.infrastructure.integration.stone.service.StoneAuthService;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.infrastructure.security.SecretEncryptionService;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.request.BankIntegrationRequest;
import com.confiapix.presentation.response.BankIntegrationResponse;
import com.confiapix.presentation.response.BankProviderCatalogItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageBankIntegrationUseCase {

    private final BankIntegrationJpaRepository integrationRepository;
    private final BankProviderRegistry providerRegistry;
    private final BrazilianBankCatalog bankCatalog;
    private final SecretEncryptionService encryptionService;
    private final BankIntegrationConfigSupport configSupport;
    private final StoneAuthService stoneAuthService;

    @Transactional(readOnly = true)
    public List<BankProviderCatalogItemResponse> listCatalog() {
        UUID tenantId = TenantContextHolder.getTenantId();
        List<BankIntegrationJpaEntity> configured = integrationRepository.findByTenantId(tenantId);

        return providerRegistry.listDescriptors().stream()
                .map(descriptor -> {
                    BankIntegrationJpaEntity integration = configured.stream()
                            .filter(item -> providerMatches(item.getProvider(), descriptor.provider()))
                            .findFirst()
                            .orElse(null);
                    return BankProviderCatalogItemResponse.builder()
                            .provider(descriptor.provider())
                            .compe(descriptor.compe())
                            .ispb(descriptor.ispb())
                            .displayName(descriptor.displayName())
                            .description(descriptor.description())
                            .available(descriptor.available())
                            .configured(integration != null)
                            .active(integration != null && integration.isActive())
                            .supportsSync(descriptor.supportsSync())
                            .supportsWebhook(descriptor.supportsWebhook())
                            .supportsConnectionTest(descriptor.supportsConnectionTest())
                            .credentialSchemaId(descriptor.credentialSchemaId())
                            .credentialSchema(descriptor.credentialSchema())
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public BankIntegrationResponse get(String provider) {
        String normalized = BankProviderCodes.normalize(provider);
        UUID tenantId = TenantContextHolder.getTenantId();
        BankIntegrationJpaEntity entity = findConfiguredIntegration(tenantId, normalized)
                .orElseThrow(() -> new BusinessException(
                        "Integração " + bankCatalog.displayName(normalized) + " não configurada"));
        return toResponse(entity);
    }

    @Transactional
    public BankIntegrationResponse upsert(String provider, BankIntegrationRequest request) {
        String normalized = BankProviderCodes.normalize(provider);
        validate(normalized, request);

        UUID tenantId = TenantContextHolder.getTenantId();
        Optional<BankIntegrationJpaEntity> existing = findConfiguredIntegration(tenantId, normalized);
        BankIntegrationJpaEntity entity = existing.orElseGet(() -> configSupport.newEntity(tenantId, normalized));

        if (request.getClientSecret() == null || request.getClientSecret().isBlank()) {
            if (existing.isEmpty()) {
                throw new BusinessException("clientSecret é obrigatório no primeiro cadastro");
            }
        } else {
            entity.setClientSecretEncrypted(encryptionService.encrypt(request.getClientSecret().trim()));
        }

        entity.setClientId(trimToNull(request.getClientId()));
        entity.setAccountRef(request.getAccountRef().trim());
        entity.setMerchantRef(trimToNull(request.getMerchantRef()));
        entity.setConfigJson(resolveConfigJson(normalized, request));
        entity.setActive(request.getActive() == null || request.getActive());

        BankIntegrationJpaEntity saved = integrationRepository.save(entity);
        if (BankProviderCodes.STONE.equals(normalized)) {
            stoneAuthService.evictToken(tenantId);
        }
        return toResponse(saved);
    }

    @Transactional
    public BankIntegrationResponse deactivate(String provider) {
        String normalized = BankProviderCodes.normalize(provider);
        UUID tenantId = TenantContextHolder.getTenantId();
        BankIntegrationJpaEntity entity = findConfiguredIntegration(tenantId, normalized)
                .orElseThrow(() -> new BusinessException(
                        "Integração " + bankCatalog.displayName(normalized) + " não configurada"));
        entity.setActive(false);
        BankIntegrationJpaEntity saved = integrationRepository.save(entity);
        if (BankProviderCodes.STONE.equals(normalized)) {
            stoneAuthService.evictToken(tenantId);
        }
        return toResponse(saved);
    }

    @Transactional
    public void remove(String provider) {
        String normalized = BankProviderCodes.normalize(provider);
        UUID tenantId = TenantContextHolder.getTenantId();
        BankIntegrationJpaEntity entity = findConfiguredIntegration(tenantId, normalized)
                .orElseThrow(() -> new BusinessException(
                        "Integração " + bankCatalog.displayName(normalized) + " não configurada"));
        integrationRepository.delete(entity);
        if (BankProviderCodes.STONE.equals(normalized)) {
            stoneAuthService.evictToken(tenantId);
        }
    }

    private Optional<BankIntegrationJpaEntity> findConfiguredIntegration(UUID tenantId, String provider) {
        return integrationRepository.findByTenantId(tenantId).stream()
                .filter(item -> providerMatches(item.getProvider(), provider))
                .findFirst();
    }

    private boolean providerMatches(String storedProvider, String catalogProvider) {
        return BankProviderCodes.normalize(storedProvider).equals(BankProviderCodes.normalize(catalogProvider));
    }

    private void validate(String provider, BankIntegrationRequest request) {
        IntegrationPluginId pluginId = BankProviderCodes.pluginFor(provider);
        if (pluginId == IntegrationPluginId.STONE) {
            validateStone(request);
            return;
        }
        if (pluginId == IntegrationPluginId.INTER || pluginId == IntegrationPluginId.C6) {
            validateOAuthLike(request);
            return;
        }
        validateGeneric(request);
    }

    private void validateStone(BankIntegrationRequest request) {
        Map<String, String> config = request.getConfig() != null ? request.getConfig() : Map.of();
        String authMode = config.getOrDefault("authMode", StoneAuthMode.OPEN_BANKING.name());
        if (StoneAuthMode.API_KEY.name().equalsIgnoreCase(authMode)
                && request.getClientSecret() != null
                && !request.getClientSecret().isBlank()
                && !request.getClientSecret().trim().startsWith("sk_")) {
            throw new BusinessException("No modo API_KEY Stone, informe SecretKey com prefixo sk_");
        }
        if (StoneAuthMode.OPEN_BANKING.name().equalsIgnoreCase(authMode)
                && (request.getClientId() == null || request.getClientId().isBlank())) {
            throw new BusinessException("clientId é obrigatório no modo OPEN_BANKING Stone");
        }
    }

    private void validateOAuthLike(BankIntegrationRequest request) {
        if (request.getClientId() == null || request.getClientId().isBlank()) {
            throw new BusinessException("clientId é obrigatório");
        }
        if (request.getAccountRef() == null || request.getAccountRef().isBlank()) {
            throw new BusinessException("Informe a conta recebedora (agência/conta ou número da conta)");
        }
    }

    private void validateGeneric(BankIntegrationRequest request) {
        if (request.getAccountRef() == null || request.getAccountRef().isBlank()) {
            throw new BusinessException("Informe o número da conta recebedora");
        }
    }

    private String resolveConfigJson(String provider, BankIntegrationRequest request) {
        Map<String, String> config = request.getConfig() != null ? request.getConfig() : Map.of();
        if (BankProviderCodes.STONE.equals(provider)) {
            StoneAuthMode authMode = StoneAuthMode.valueOf(
                    config.getOrDefault("authMode", StoneAuthMode.OPEN_BANKING.name()).trim().toUpperCase());
            StoneBusinessModel businessModel = StoneBusinessModel.valueOf(
                    config.getOrDefault("businessModel", StoneBusinessModel.GATEWAY.name()).trim().toUpperCase());
            return configSupport.writeStoneConfig(authMode, businessModel);
        }
        return configSupport.writeGenericConfig(config);
    }

    private BankIntegrationResponse toResponse(BankIntegrationJpaEntity entity) {
        return BankIntegrationResponse.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .provider(entity.getProvider())
                .clientId(entity.getClientId())
                .accountRef(entity.getAccountRef())
                .merchantRef(entity.getMerchantRef())
                .config(configSupport.readConfig(entity))
                .active(entity.isActive())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

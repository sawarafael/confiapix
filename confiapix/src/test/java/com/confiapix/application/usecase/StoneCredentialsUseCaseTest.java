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
import com.confiapix.infrastructure.tenant.TenantContext;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.request.StoneCredentialsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoneCredentialsUseCaseTest {

    @Mock
    private BankIntegrationJpaRepository integrationRepository;

    @Mock
    private SecretEncryptionService encryptionService;

    @Mock
    private StoneAuthService stoneAuthService;

    @Spy
    private BankIntegrationConfigSupport configSupport = new BankIntegrationConfigSupport(new ObjectMapper());

    @InjectMocks
    private StoneCredentialsUseCase stoneCredentialsUseCase;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        TenantContextHolder.set(new TenantContext(tenantId, UUID.randomUUID(), "test@confiapix.test"));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldUpsertApiKeyCredentials() {
        StoneCredentialsRequest request = new StoneCredentialsRequest();
        request.setAuthMode("API_KEY");
        request.setBusinessModel("GATEWAY");
        request.setClientSecret("sk_test_secret_key_123456");
        request.setAccountId("194047458");

        when(integrationRepository.findByTenantIdAndProvider(tenantId, BankProviderCodes.STONE)).thenReturn(Optional.empty());
        when(encryptionService.encrypt("sk_test_secret_key_123456")).thenReturn("enc-secret");
        when(integrationRepository.save(any())).thenAnswer(invocation -> {
            BankIntegrationJpaEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        var response = stoneCredentialsUseCase.upsert(request);

        assertThat(response.getAuthMode()).isEqualTo("API_KEY");
        assertThat(response.getAccountId()).isEqualTo("194047458");
        assertThat(response.getClientId()).isEqualTo("194047458");
        verify(stoneAuthService).evictToken(tenantId);
    }

    @Test
    void shouldUpsertOpenBankingCredentials() {
        StoneCredentialsRequest request = new StoneCredentialsRequest();
        request.setAuthMode("OPEN_BANKING");
        request.setClientId("client");
        request.setClientSecret("secret");
        request.setAccountId("account-1");
        request.setMerchantId("merchant-1");

        when(integrationRepository.findByTenantIdAndProvider(tenantId, BankProviderCodes.STONE)).thenReturn(Optional.empty());
        when(encryptionService.encrypt("secret")).thenReturn("enc-secret");
        when(integrationRepository.save(any())).thenAnswer(invocation -> {
            BankIntegrationJpaEntity entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            return entity;
        });

        var response = stoneCredentialsUseCase.upsert(request);

        assertThat(response.getClientId()).isEqualTo("client");
        assertThat(response.getAuthMode()).isEqualTo("OPEN_BANKING");
        assertThat(response.getAccountId()).isEqualTo("account-1");
        verify(stoneAuthService).evictToken(tenantId);
    }

    @Test
    void shouldThrowWhenCredentialsNotConfigured() {
        when(integrationRepository.findByTenantIdAndProvider(tenantId, BankProviderCodes.STONE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stoneCredentialsUseCase.getCurrent())
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciais Stone não configuradas");
    }
}

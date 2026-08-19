package com.confiapix.infrastructure.integration.stone.service;

import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.bank.BankIntegrationConfigSupport;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.infrastructure.security.SecretEncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StoneAuthServiceTest {

    private StoneAuthService stoneAuthService;
    private MockRestServiceServer mockServer;
    private UUID tenantId;
    private BankIntegrationConfigSupport configSupport;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        StoneProperties properties = new StoneProperties();
        properties.setTokenUrl("https://stone.test/token");
        configSupport = new BankIntegrationConfigSupport(new ObjectMapper());

        SecretEncryptionService encryptionService = new SecretEncryptionService(
                "test-encryption-secret-for-unit-tests-32chars-minimum");

        BankIntegrationJpaRepository repository = org.mockito.Mockito.mock(BankIntegrationJpaRepository.class);
        BankIntegrationJpaEntity credentials = BankIntegrationJpaEntity.builder()
                .tenantId(tenantId)
                .provider(BankProviderCodes.STONE)
                .clientId("client-id")
                .clientSecretEncrypted(encryptionService.encrypt("client-secret"))
                .accountRef("account-id")
                .configJson(configSupport.writeStoneConfig(StoneAuthMode.OPEN_BANKING, StoneBusinessModel.GATEWAY))
                .active(true)
                .build();

        org.mockito.Mockito.when(repository.findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE))
                .thenReturn(Optional.of(credentials));

        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        stoneAuthService = new StoneAuthService(repository, configSupport, encryptionService, properties, builder);
    }

    @Test
    void shouldObtainAndCacheAccessToken() {
        mockServer.expect(requestTo("https://stone.test/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"access_token":"token-abc","expires_in":3600,"token_type":"Bearer"}
                        """, MediaType.APPLICATION_JSON));

        String first = stoneAuthService.getAccessToken(tenantId);
        String second = stoneAuthService.getAccessToken(tenantId);

        assertThat(first).isEqualTo("token-abc");
        assertThat(second).isEqualTo("token-abc");
        mockServer.verify();
    }

    @Test
    void shouldResolveApiKeyAuth() {
        SecretEncryptionService encryptionService = new SecretEncryptionService(
                "test-encryption-secret-for-unit-tests-32chars-minimum");
        BankIntegrationJpaRepository repository = org.mockito.Mockito.mock(BankIntegrationJpaRepository.class);
        BankIntegrationJpaEntity credentials = BankIntegrationJpaEntity.builder()
                .tenantId(tenantId)
                .provider(BankProviderCodes.STONE)
                .clientSecretEncrypted(encryptionService.encrypt("sk_test_key"))
                .accountRef("194047458")
                .configJson(configSupport.writeStoneConfig(StoneAuthMode.API_KEY, StoneBusinessModel.GATEWAY))
                .active(true)
                .build();
        org.mockito.Mockito.when(repository.findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE))
                .thenReturn(Optional.of(credentials));

        StoneProperties properties = new StoneProperties();
        StoneAuthService service = new StoneAuthService(
                repository, configSupport, encryptionService, properties, RestClient.builder());

        var auth = service.resolveAuth(tenantId);

        assertThat(auth.authMode()).isEqualTo(StoneAuthMode.API_KEY);
        assertThat(auth.authorizationHeader()).startsWith("Basic ");
        assertThat(auth.extraHeaders().get("Host")).isEqualTo("sdx-ecommerce-payments.stone.com.br");
        assertThat(auth.extraHeaders().get("x-stone-account-id")).isEqualTo("194047458");
    }
}

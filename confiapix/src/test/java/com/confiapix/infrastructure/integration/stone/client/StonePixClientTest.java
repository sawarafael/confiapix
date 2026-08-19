package com.confiapix.infrastructure.integration.stone.client;

import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.config.StoneProperties;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthContext;
import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.service.StoneAuthService;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
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

class StonePixClientTest {

    private StonePixClient stonePixClient;
    private MockRestServiceServer mockServer;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        StoneProperties properties = new StoneProperties();
        properties.setBaseUrl("https://stone.test");

        BankIntegrationJpaRepository repository = org.mockito.Mockito.mock(BankIntegrationJpaRepository.class);
        BankIntegrationJpaEntity credentials = BankIntegrationJpaEntity.builder()
                .tenantId(tenantId)
                .provider(BankProviderCodes.STONE)
                .accountRef("account-1")
                .clientSecretEncrypted("encrypted")
                .active(true)
                .build();
        org.mockito.Mockito.when(repository.findByTenantIdAndProviderAndActiveTrue(tenantId, BankProviderCodes.STONE))
                .thenReturn(Optional.of(credentials));

        StoneAuthService authService = org.mockito.Mockito.mock(StoneAuthService.class);
        org.mockito.Mockito.when(authService.resolveAuth(tenantId))
                .thenReturn(new StoneAuthContext("Bearer token-abc", java.util.Map.of(), StoneAuthMode.OPEN_BANKING));

        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        stonePixClient = new StonePixClient(authService, repository, properties, builder, new ObjectMapper());
    }

    @Test
    void shouldFindPixByTxid() {
        mockServer.expect(requestTo("https://stone.test/api/v1/pix/account-1/pix_payments?transaction_ids=TX-123&limit=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "data": [{
                            "id": "pix-1",
                            "transaction_id": "TX-123",
                            "amount": 10000,
                            "status": "SETTLED",
                            "type": "inbound_pix_payment",
                            "created_at": "2024-06-10T12:00:00Z"
                          }]
                        }
                        """, MediaType.APPLICATION_JSON));

        var result = stonePixClient.findPixByTxid(tenantId, "TX-123");

        assertThat(result).isPresent();
        assertThat(result.get().getTxid().value()).isEqualTo("TX-123");
        mockServer.verify();
    }
}

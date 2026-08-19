package com.confiapix.application.usecase;

import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.dto.StonePixEntity;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookCounterParty;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookTargetData;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessStoneWebhookUseCaseTest {

    @Mock
    private BankIntegrationJpaRepository integrationRepository;

    @Mock
    private ImportPixUseCase importPixUseCase;

    private ProcessStoneWebhookUseCase processStoneWebhookUseCase;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        processStoneWebhookUseCase = new ProcessStoneWebhookUseCase(
                integrationRepository, importPixUseCase, new ObjectMapper());
    }

    @Test
    void shouldProcessPixInboundWebhook() {
        StoneWebhookPayload payload = new StoneWebhookPayload(
                "sandbox",
                "pix_inbound_payment_received",
                "evt-1",
                "2024-06-10T12:00:00Z",
                "2024-06-10T12:00:01Z",
                new StoneWebhookTargetData(
                        "account-123",
                        10000L,
                        "pix-id",
                        "SETTLED",
                        "E123",
                        "TX-WEBHOOK-1",
                        "2024-06-10T12:00:00Z",
                        "2024-06-10T12:00:01Z",
                        new StoneWebhookCounterParty(new StonePixEntity("12345678901", "Maria", "cpf"))),
                null);

        when(integrationRepository.findFirstByProviderAndAccountRefAndActiveTrue(BankProviderCodes.STONE, "account-123"))
                .thenReturn(Optional.of(BankIntegrationJpaEntity.builder()
                        .tenantId(tenantId)
                        .provider(BankProviderCodes.STONE)
                        .accountRef("account-123")
                        .active(true)
                        .build()));
        when(importPixUseCase.importAndReconcile(eq(tenantId), any()))
                .thenReturn(ImportPixUseCase.ImportPixResult.imported(Optional.empty()));

        var result = processStoneWebhookUseCase.process(payload);

        assertThat(result.isProcessed()).isTrue();
        assertThat(result.isImported()).isTrue();
        assertThat(result.getTxid()).isEqualTo("TX-WEBHOOK-1");
    }

    @Test
    void shouldIgnoreNonPixEvents() {
        StoneWebhookPayload payload = new StoneWebhookPayload(
                "sandbox", "cash_in_internal_transfer", "evt-2",
                null, null, null, null);

        var result = processStoneWebhookUseCase.process(payload);

        assertThat(result.isProcessed()).isFalse();
    }
}

package com.confiapix.application.usecase;

import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.confiapix.infrastructure.integration.stone.mapper.StoneWebhookMapper;
import com.confiapix.infrastructure.persistence.entity.BankIntegrationJpaEntity;
import com.confiapix.infrastructure.persistence.repository.BankIntegrationJpaRepository;
import com.confiapix.presentation.response.StoneWebhookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessStoneWebhookUseCase {

    private final BankIntegrationJpaRepository integrationRepository;
    private final ImportPixUseCase importPixUseCase;
    private final ObjectMapper objectMapper;

    @Transactional
    public StoneWebhookResponse process(StoneWebhookPayload payload) {
        MDC.put("eventType", payload.eventType());

        if (!payload.isPixInboundEvent() || payload.targetData() == null) {
            log.debug("Webhook Stone ignorado eventType={}", payload.eventType());
            return StoneWebhookResponse.ignored(payload.eventType());
        }

        String accountId = payload.targetData().accountId();
        if (accountId == null || accountId.isBlank()) {
            throw new BusinessException("Webhook Stone sem account_id");
        }

        UUID tenantId = resolveTenantId(accountId);
        MDC.put("tenantId", tenantId.toString());

        PixTransaction candidate = StoneWebhookMapper.toDomain(tenantId, payload, objectMapper);
        MDC.put("txid", candidate.getTxid().value());

        ImportPixUseCase.ImportPixResult result = importPixUseCase.importAndReconcile(tenantId, candidate);

        log.info("Webhook Stone processado eventType={} tenantId={} txid={} imported={} reconciled={}",
                payload.eventType(), tenantId, candidate.getTxid().value(), result.imported(), result.reconciled());

        return StoneWebhookResponse.builder()
                .eventType(payload.eventType())
                .processed(true)
                .imported(result.imported())
                .skipped(result.skipped())
                .reconciled(result.reconciled())
                .txid(candidate.getTxid().value())
                .build();
    }

    private UUID resolveTenantId(String accountId) {
        return integrationRepository.findFirstByProviderAndAccountRefAndActiveTrue(BankProviderCodes.STONE, accountId)
                .or(() -> integrationRepository.findFirstByProviderAndMerchantRefAndActiveTrue(BankProviderCodes.STONE, accountId))
                .map(BankIntegrationJpaEntity::getTenantId)
                .orElseThrow(() -> new BusinessException("Tenant não encontrado para account_id: " + accountId));
    }
}

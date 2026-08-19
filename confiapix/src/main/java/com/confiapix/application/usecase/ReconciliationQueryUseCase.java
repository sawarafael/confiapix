package com.confiapix.application.usecase;

import com.confiapix.application.mapper.PixResponseMapper;
import com.confiapix.domain.entity.Reconciliation;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.ReconciliationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReconciliationQueryUseCase {

    private final ReconciliationRepositoryPort reconciliationRepository;
    private final PixTransactionRepositoryPort pixTransactionRepository;

    @Transactional(readOnly = true)
    public List<ReconciliationResponse> list() {
        UUID tenantId = TenantContextHolder.getTenantId();
        return reconciliationRepository.findByTenantId(tenantId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReconciliationResponse findById(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        Reconciliation reconciliation = reconciliationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new BusinessException("Conciliação não encontrada"));
        return toResponse(reconciliation);
    }

    private ReconciliationResponse toResponse(Reconciliation reconciliation) {
        String pixTxid = pixTransactionRepository
                .findByIdAndTenantId(reconciliation.getPixTransactionId(), reconciliation.getTenantId())
                .map(pix -> pix.getTxid().value())
                .orElse(null);
        return PixResponseMapper.toResponse(reconciliation, pixTxid);
    }
}

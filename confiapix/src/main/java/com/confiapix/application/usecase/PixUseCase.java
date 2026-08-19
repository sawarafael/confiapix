package com.confiapix.application.usecase;

import com.confiapix.application.mapper.PixDetailMapper;
import com.confiapix.application.mapper.PixResponseMapper;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.PixDetailResponse;
import com.confiapix.presentation.response.PixResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PixUseCase {

    private final PixTransactionRepositoryPort pixTransactionRepository;
    private final ReconciliationRepositoryPort reconciliationRepository;
    private final PixDetailMapper pixDetailMapper;

    @Transactional(readOnly = true)
    public Page<PixResponse> list(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return pixTransactionRepository.findByTenantId(tenantId, pageable)
                .map(PixResponseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PixResponse findByTxid(String txid) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return pixTransactionRepository.findByTenantIdAndTxid(tenantId, Txid.of(txid))
                .map(PixResponseMapper::toResponse)
                .orElseThrow(() -> new BusinessException("PIX não encontrado"));
    }

    @Transactional(readOnly = true)
    public PixDetailResponse findDetailById(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return pixTransactionRepository.findByIdAndTenantId(id, tenantId)
                .map(pix -> pixDetailMapper.toDetail(
                        pix,
                        reconciliationRepository.findByPixTransactionId(pix.getId()).orElse(null)))
                .orElseThrow(() -> new BusinessException("PIX não encontrado"));
    }

    @Transactional(readOnly = true)
    public PixDetailResponse findDetailByTxid(String txid) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return pixTransactionRepository.findByTenantIdAndTxid(tenantId, Txid.of(txid))
                .map(pix -> pixDetailMapper.toDetail(
                        pix,
                        reconciliationRepository.findByPixTransactionId(pix.getId()).orElse(null)))
                .orElseThrow(() -> new BusinessException("PIX não encontrado"));
    }

    @Transactional(readOnly = true)
    public PixResponse findById(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return pixTransactionRepository.findByIdAndTenantId(id, tenantId)
                .map(PixResponseMapper::toResponse)
                .orElseThrow(() -> new BusinessException("PIX não encontrado"));
    }
}

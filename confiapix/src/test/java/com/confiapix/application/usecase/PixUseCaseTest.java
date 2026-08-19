package com.confiapix.application.usecase;

import com.confiapix.application.mapper.PixDetailMapper;
import com.confiapix.domain.repository.ReconciliationRepositoryPort;
import com.confiapix.domain.entity.PixTransaction;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.repository.PixTransactionRepositoryPort;
import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.PixSource;
import com.confiapix.domain.valueobject.Txid;
import com.confiapix.infrastructure.tenant.TenantContext;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.PixDetailResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PixUseCaseTest {

    @Mock
    private PixTransactionRepositoryPort pixTransactionRepository;

    @Mock
    private ReconciliationRepositoryPort reconciliationRepository;

    @Mock
    private PixDetailMapper pixDetailMapper;

    @InjectMocks
    private PixUseCase pixUseCase;

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
    void shouldListPixByTenant() {
        PixTransaction pix = samplePix("TX-1");
        when(pixTransactionRepository.findByTenantId(tenantId, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(pix)));

        var page = pixUseCase.list(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getTxid()).isEqualTo("TX-1");
    }

    @Test
    void shouldFindPixDetailById() {
        PixTransaction pix = samplePix("TX-2");
        PixDetailResponse detail = PixDetailResponse.builder().txid("TX-2").build();

        when(pixTransactionRepository.findByIdAndTenantId(pix.getId(), tenantId))
                .thenReturn(Optional.of(pix));
        when(reconciliationRepository.findByPixTransactionId(pix.getId()))
                .thenReturn(Optional.empty());
        when(pixDetailMapper.toDetail(pix, null)).thenReturn(detail);

        var response = pixUseCase.findDetailById(pix.getId());

        assertThat(response.getTxid()).isEqualTo("TX-2");
    }

    @Test
    void shouldFindPixByTxid() {
        when(pixTransactionRepository.findByTenantIdAndTxid(tenantId, Txid.of("TX-2")))
                .thenReturn(Optional.of(samplePix("TX-2")));

        var response = pixUseCase.findByTxid("TX-2");

        assertThat(response.getTxid()).isEqualTo("TX-2");
    }

    @Test
    void shouldThrowWhenPixNotFound() {
        when(pixTransactionRepository.findByTenantIdAndTxid(tenantId, Txid.of("missing")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pixUseCase.findByTxid("missing"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("PIX não encontrado");
    }

    private PixTransaction samplePix(String txid) {
        PixTransaction pix = new PixTransaction();
        pix.setId(UUID.randomUUID());
        pix.setTenantId(tenantId);
        pix.setTxid(Txid.of(txid));
        pix.setAmount(Money.of(BigDecimal.TEN));
        pix.setReceivedAt(Instant.now());
        pix.setSource(PixSource.WEBHOOK);
        return pix;
    }
}

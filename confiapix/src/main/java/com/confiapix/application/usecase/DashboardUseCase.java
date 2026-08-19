package com.confiapix.application.usecase;

import com.confiapix.domain.valueobject.AccountStatus;
import com.confiapix.presentation.response.DashboardResponse;
import com.confiapix.infrastructure.persistence.repository.AccountPayableRepository;
import com.confiapix.infrastructure.persistence.repository.AccountReceivableRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardUseCase {

    private static final List<AccountStatus> OPEN_STATUSES = List.of(
            AccountStatus.PENDING, AccountStatus.OVERDUE);

    private final AccountReceivableRepository receivableRepository;
    private final AccountPayableRepository payableRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        UUID tenantId = TenantContextHolder.getTenantId();
        LocalDate today = LocalDate.now();

        BigDecimal totalReceivable = receivableRepository.sumAmountByTenantIdAndStatusIn(tenantId, OPEN_STATUSES);
        BigDecimal totalPayable = payableRepository.sumAmountByTenantIdAndStatusIn(tenantId, OPEN_STATUSES);

        BigDecimal overdueReceivableStatus = receivableRepository.sumAmountByTenantIdAndStatusIn(
                tenantId, List.of(AccountStatus.OVERDUE));
        BigDecimal overdueReceivablePending = receivableRepository.sumOverduePending(tenantId, today);
        BigDecimal overdueReceivable = overdueReceivableStatus.add(overdueReceivablePending);

        BigDecimal overduePayableStatus = payableRepository.sumAmountByTenantIdAndStatusIn(
                tenantId, List.of(AccountStatus.OVERDUE));
        BigDecimal overduePayablePending = payableRepository.sumOverduePending(tenantId, today);
        BigDecimal overduePayable = overduePayableStatus.add(overduePayablePending);

        BigDecimal projectedBalance = totalReceivable.subtract(totalPayable);

        return DashboardResponse.builder()
                .totalReceivable(totalReceivable)
                .totalPayable(totalPayable)
                .overdueReceivable(overdueReceivable)
                .overduePayable(overduePayable)
                .projectedBalance(projectedBalance)
                .build();
    }
}

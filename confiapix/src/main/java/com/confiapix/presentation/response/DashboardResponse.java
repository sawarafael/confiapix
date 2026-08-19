package com.confiapix.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DashboardResponse {

    private BigDecimal totalReceivable;
    private BigDecimal totalPayable;
    private BigDecimal overdueReceivable;
    private BigDecimal overduePayable;
    private BigDecimal projectedBalance;
}

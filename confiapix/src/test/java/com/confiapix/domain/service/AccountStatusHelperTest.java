package com.confiapix.domain.service;

import com.confiapix.domain.valueobject.AccountStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AccountStatusHelperTest {

    @Test
    void shouldKeepTerminalStatuses() {
        assertThat(AccountStatusHelper.resolveStatus(AccountStatus.PAID, LocalDate.now().minusDays(1)))
                .isEqualTo(AccountStatus.PAID);
        assertThat(AccountStatusHelper.resolveStatus(AccountStatus.CANCELED, LocalDate.now().minusDays(1)))
                .isEqualTo(AccountStatus.CANCELED);
    }

    @Test
    void shouldMarkOverdueWhenDueDatePassed() {
        assertThat(AccountStatusHelper.resolveStatus(AccountStatus.PENDING, LocalDate.now().minusDays(2)))
                .isEqualTo(AccountStatus.OVERDUE);
    }

    @Test
    void shouldKeepPendingWhenDueDateNotPassed() {
        assertThat(AccountStatusHelper.resolveStatus(AccountStatus.PENDING, LocalDate.now().plusDays(2)))
                .isEqualTo(AccountStatus.PENDING);
    }
}

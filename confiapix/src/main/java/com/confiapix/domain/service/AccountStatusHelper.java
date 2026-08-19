package com.confiapix.domain.service;

import com.confiapix.domain.valueobject.AccountStatus;

import java.time.LocalDate;

public final class AccountStatusHelper {

    private AccountStatusHelper() {
    }

    public static AccountStatus resolveStatus(AccountStatus currentStatus, LocalDate dueDate) {
        if (currentStatus == AccountStatus.PAID || currentStatus == AccountStatus.CANCELED) {
            return currentStatus;
        }
        if (dueDate.isBefore(LocalDate.now())) {
            return AccountStatus.OVERDUE;
        }
        return AccountStatus.PENDING;
    }
}

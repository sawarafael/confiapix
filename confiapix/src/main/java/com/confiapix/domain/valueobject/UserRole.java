package com.confiapix.domain.valueobject;

public enum UserRole {
    ADMIN,
    FINANCIAL,
    VIEWER;

    public String authority() {
        return "ROLE_" + name();
    }
}

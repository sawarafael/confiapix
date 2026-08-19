package com.confiapix.infrastructure.tenant;

import com.confiapix.domain.exception.BusinessException;

import java.util.UUID;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void set(TenantContext context) {
        CONTEXT.set(context);
    }

    public static TenantContext get() {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            throw new BusinessException("Contexto de tenant não disponível");
        }
        return context;
    }

    public static UUID getTenantId() {
        return get().getTenantId();
    }

    public static UUID getUserId() {
        return get().getUserId();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

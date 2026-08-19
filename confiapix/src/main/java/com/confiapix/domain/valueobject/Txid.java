package com.confiapix.domain.valueobject;

import com.confiapix.domain.exception.DomainException;

import java.util.Objects;

public final class Txid {

    private static final int MAX_LENGTH = 35;

    private final String value;

    private Txid(String value) {
        this.value = value;
    }

    public static Txid of(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("TXID do PIX é obrigatório");
        }
        String normalized = value.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new DomainException("TXID do PIX excede o tamanho máximo de " + MAX_LENGTH + " caracteres");
        }
        return new Txid(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Txid txid)) {
            return false;
        }
        return Objects.equals(value, txid.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

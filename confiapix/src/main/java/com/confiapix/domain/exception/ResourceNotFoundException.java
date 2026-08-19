package com.confiapix.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(String.format("%s não encontrado(a): %s", resource, id));
    }
}

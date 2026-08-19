package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
    private long expiresIn;
    private UUID userId;
    private UUID tenantId;
    private String email;
    private String name;
    private UserRole role;
    private boolean platformOperator;
}

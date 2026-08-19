package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserProfileResponse {

    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private boolean active;
    private UUID tenantId;
    private String tenantName;
    private String plan;
    private boolean platformOperator;
    private Instant createdAt;
    private Instant updatedAt;
}

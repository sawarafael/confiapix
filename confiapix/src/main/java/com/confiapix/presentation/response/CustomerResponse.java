package com.confiapix.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CustomerResponse {

    private UUID id;
    private String name;
    private String document;
    private String email;
    private String phone;
    private UUID companyId;
    private String companyName;
    private Instant createdAt;
}

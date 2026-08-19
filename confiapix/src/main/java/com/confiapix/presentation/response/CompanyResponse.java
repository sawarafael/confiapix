package com.confiapix.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CompanyResponse {

    private UUID id;
    private String corporateName;
    private String tradeName;
    private String document;
    private boolean active;
    private Instant createdAt;
}

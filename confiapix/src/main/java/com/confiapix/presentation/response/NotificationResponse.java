package com.confiapix.presentation.response;

import com.confiapix.domain.valueobject.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class NotificationResponse {

    private UUID id;
    private NotificationType type;
    private String title;
    private String message;

    @JsonProperty("read")
    private boolean read;

    private UUID referenceId;
    private String referenceType;
    private Instant createdAt;
}

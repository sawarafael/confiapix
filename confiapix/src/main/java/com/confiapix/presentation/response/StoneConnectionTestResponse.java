package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoneConnectionTestResponse {

    private String authMode;
    private boolean success;
    private int httpStatus;
    private String message;
    private String endpointTested;
    private String hostHeader;
    private String responsePreview;
}

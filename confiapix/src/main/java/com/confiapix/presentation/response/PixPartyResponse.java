package com.confiapix.presentation.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PixPartyResponse {

    private String name;
    private String document;
    private String documentType;
}

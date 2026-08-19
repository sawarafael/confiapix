package com.confiapix.infrastructure.integration.bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrazilianBankInstitution {

    private String compe;
    private String ispb;
    private String name;
    private String shortName;
}

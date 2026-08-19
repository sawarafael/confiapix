package com.confiapix.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankSyncResponse {

    private String provider;
    private int fetched;
    private int imported;
    private int reconciled;
}

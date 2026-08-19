package com.confiapix.application.port.bank;

import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.IntegrationPluginId;

import java.util.List;

public interface BankProviderRegistry {

    BankIntegrationPlugin require(IntegrationPluginId pluginId);

    BankIntegrationPlugin requireForProvider(String providerCode);

    List<BankProviderDescriptor> listDescriptors();
}

package com.confiapix.infrastructure.integration.bank;

import com.confiapix.application.port.bank.BankIntegrationPlugin;
import com.confiapix.application.port.bank.BankProviderDescriptor;
import com.confiapix.application.port.bank.BankProviderRegistry;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.BankProviderCodes;
import com.confiapix.domain.valueobject.IntegrationPluginId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BankProviderRegistryImpl implements BankProviderRegistry {

    private final Map<IntegrationPluginId, BankIntegrationPlugin> plugins;
    private final BrazilianBankCatalog bankCatalog;

    public BankProviderRegistryImpl(List<BankIntegrationPlugin> pluginList, BrazilianBankCatalog bankCatalog) {
        this.plugins = pluginList.stream()
                .collect(Collectors.toUnmodifiableMap(BankIntegrationPlugin::pluginId, Function.identity()));
        this.bankCatalog = bankCatalog;
    }

    @Override
    public BankIntegrationPlugin require(IntegrationPluginId pluginId) {
        BankIntegrationPlugin plugin = plugins.get(pluginId);
        if (plugin == null) {
            throw new BusinessException("Provedor bancário não suportado: " + pluginId.name());
        }
        return plugin;
    }

    @Override
    public BankIntegrationPlugin requireForProvider(String providerCode) {
        return require(BankProviderCodes.pluginFor(providerCode));
    }

    @Override
    public List<BankProviderDescriptor> listDescriptors() {
        List<BankProviderDescriptor> descriptors = new ArrayList<>();

        descriptors.add(buildDescriptor(
                BankProviderCodes.STONE,
                null,
                null,
                plugins.get(IntegrationPluginId.STONE).descriptorTemplate()));

        for (BrazilianBankInstitution bank : bankCatalog.getInstitutions()) {
            if (BankProviderCodes.STONE.equalsIgnoreCase(bank.getCompe())
                    || "197".equals(BankProviderCodes.normalize(bank.getCompe()))) {
                continue;
            }
            IntegrationPluginId pluginId = BankProviderCodes.pluginFor(bank.getCompe());
            BankIntegrationPlugin plugin = plugins.get(pluginId);
            BankProviderDescriptor template = plugin != null
                    ? plugin.descriptorTemplate()
                    : plugins.get(IntegrationPluginId.GENERIC).descriptorTemplate();
            descriptors.add(buildDescriptor(bank.getCompe(), bank.getCompe(), bank.getIspb(), template, bank));
        }

        return descriptors.stream()
                .sorted(Comparator.comparing(BankProviderDescriptor::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private BankProviderDescriptor buildDescriptor(
            String provider,
            String compe,
            String ispb,
            BankProviderDescriptor template) {
        return buildDescriptor(provider, compe, ispb, template, null);
    }

    private BankProviderDescriptor buildDescriptor(
            String provider,
            String compe,
            String ispb,
            BankProviderDescriptor template,
            BrazilianBankInstitution bank) {
        String displayName = bank != null ? bank.getName() : template.displayName();
        String description = bank != null
                ? "Integração PIX — " + bank.getShortName() + " (COMPE " + bank.getCompe() + ")"
                : template.description();
        return new BankProviderDescriptor(
                provider,
                compe,
                ispb,
                displayName,
                description,
                template.available(),
                template.supportsSync(),
                template.supportsWebhook(),
                template.supportsConnectionTest(),
                template.credentialSchemaId(),
                template.credentialSchema());
    }
}

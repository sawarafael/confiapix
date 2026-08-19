package com.confiapix.infrastructure.integration.stone.config;

import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "confiapix.stone")
public class StoneProperties {

    private String baseUrl = "https://sandbox-api.openbank.stone.com.br";
    private String tokenUrl = "https://sandbox.openbank.stone.com.br/auth/realms/stone_bank/protocol/openid-connect/token";
    private long pixSyncIntervalMs = 300_000L;
    private int defaultPixPageLimit = 50;
    private long tokenRefreshBufferSeconds = 60L;
    private String webhookPrivateKeyPem;
    private String webhookPublicKeyPem;
    private String paymentsBaseUrl = "https://payments.stone.com.br";
    private String sandboxGatewayHost = "sdx-ecommerce-payments.stone.com.br";
    private String sandboxSubacquirerHost = "sdx-payments.stone.com.br";
    private String productionGatewayHost = "ecommerce-payments.stone.com.br";
    private String productionSubacquirerHost = "payments.stone.com.br";
    private boolean sandbox = true;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public long getPixSyncIntervalMs() {
        return pixSyncIntervalMs;
    }

    public void setPixSyncIntervalMs(long pixSyncIntervalMs) {
        this.pixSyncIntervalMs = pixSyncIntervalMs;
    }

    public int getDefaultPixPageLimit() {
        return defaultPixPageLimit;
    }

    public void setDefaultPixPageLimit(int defaultPixPageLimit) {
        this.defaultPixPageLimit = defaultPixPageLimit;
    }

    public long getTokenRefreshBufferSeconds() {
        return tokenRefreshBufferSeconds;
    }

    public void setTokenRefreshBufferSeconds(long tokenRefreshBufferSeconds) {
        this.tokenRefreshBufferSeconds = tokenRefreshBufferSeconds;
    }

    public String getWebhookPrivateKeyPem() {
        return webhookPrivateKeyPem;
    }

    public void setWebhookPrivateKeyPem(String webhookPrivateKeyPem) {
        this.webhookPrivateKeyPem = webhookPrivateKeyPem;
    }

    public String getWebhookPublicKeyPem() {
        return webhookPublicKeyPem;
    }

    public void setWebhookPublicKeyPem(String webhookPublicKeyPem) {
        this.webhookPublicKeyPem = webhookPublicKeyPem;
    }

    public String getPaymentsBaseUrl() {
        return paymentsBaseUrl;
    }

    public void setPaymentsBaseUrl(String paymentsBaseUrl) {
        this.paymentsBaseUrl = paymentsBaseUrl;
    }

    public String getSandboxGatewayHost() {
        return sandboxGatewayHost;
    }

    public void setSandboxGatewayHost(String sandboxGatewayHost) {
        this.sandboxGatewayHost = sandboxGatewayHost;
    }

    public String getSandboxSubacquirerHost() {
        return sandboxSubacquirerHost;
    }

    public void setSandboxSubacquirerHost(String sandboxSubacquirerHost) {
        this.sandboxSubacquirerHost = sandboxSubacquirerHost;
    }

    public String getProductionGatewayHost() {
        return productionGatewayHost;
    }

    public void setProductionGatewayHost(String productionGatewayHost) {
        this.productionGatewayHost = productionGatewayHost;
    }

    public String getProductionSubacquirerHost() {
        return productionSubacquirerHost;
    }

    public void setProductionSubacquirerHost(String productionSubacquirerHost) {
        this.productionSubacquirerHost = productionSubacquirerHost;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public String resolvePaymentsHost(StoneBusinessModel businessModel) {
        if (businessModel == StoneBusinessModel.SUBACQUIRER) {
            return sandbox ? sandboxSubacquirerHost : productionSubacquirerHost;
        }
        return sandbox ? sandboxGatewayHost : productionGatewayHost;
    }
}

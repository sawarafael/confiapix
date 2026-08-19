-- Integrações bancárias multi-provedor por tenant
CREATE TABLE bank_integrations (
    id                      UUID PRIMARY KEY,
    tenant_id               UUID         NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    provider                VARCHAR(30)  NOT NULL,
    client_id               VARCHAR(255),
    client_secret_encrypted VARCHAR(512) NOT NULL,
    account_ref             VARCHAR(100),
    merchant_ref            VARCHAR(100),
    config_json             JSONB,
    active                  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_bank_integrations_tenant_provider UNIQUE (tenant_id, provider),
    CONSTRAINT chk_bank_integrations_provider CHECK (provider IN ('STONE', 'INTER', 'C6'))
);

CREATE INDEX idx_bank_integrations_tenant_id ON bank_integrations (tenant_id);
CREATE INDEX idx_bank_integrations_provider_account ON bank_integrations (provider, account_ref) WHERE active = TRUE;
CREATE INDEX idx_bank_integrations_provider_merchant ON bank_integrations (provider, merchant_ref) WHERE active = TRUE;

-- Migra credenciais Stone existentes
INSERT INTO bank_integrations (
    id,
    tenant_id,
    provider,
    client_id,
    client_secret_encrypted,
    account_ref,
    merchant_ref,
    config_json,
    active,
    created_at,
    updated_at
)
SELECT
    id,
    tenant_id,
    'STONE',
    client_id,
    client_secret_encrypted,
    account_id,
    merchant_id,
    jsonb_build_object(
        'authMode', auth_mode,
        'businessModel', business_model
    ),
    active,
    created_at,
    updated_at
FROM stone_credentials;

ALTER TABLE pix_transactions
    ADD COLUMN provider VARCHAR(30) NOT NULL DEFAULT 'STONE';

ALTER TABLE pix_transactions
    ADD CONSTRAINT chk_pix_provider CHECK (provider IN ('STONE', 'INTER', 'C6'));

CREATE INDEX idx_pix_transactions_provider ON pix_transactions (tenant_id, provider);

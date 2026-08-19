-- Perfis de acesso
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'FINANCIAL';

ALTER TABLE users
    ADD CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'FINANCIAL', 'VIEWER'));

-- Refresh tokens (rotação segura com hash SHA-256)
CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_refresh_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);

-- Credenciais Stone por tenant
CREATE TABLE stone_credentials (
    id                      UUID PRIMARY KEY,
    tenant_id               UUID         NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    client_id               VARCHAR(255) NOT NULL,
    client_secret_encrypted VARCHAR(512) NOT NULL,
    merchant_id             VARCHAR(100),
    active                  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_stone_credentials_tenant UNIQUE (tenant_id)
);

-- PIX recebidos (Stone, webhook ou sincronização)
CREATE TABLE pix_transactions (
    id             UUID PRIMARY KEY,
    tenant_id      UUID           NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    company_id     UUID           REFERENCES companies (id),
    txid           VARCHAR(35)    NOT NULL,
    end_to_end_id  VARCHAR(32),
    amount         NUMERIC(19, 2) NOT NULL,
    payer_name     VARCHAR(255),
    payer_document VARCHAR(20),
    received_at    TIMESTAMPTZ    NOT NULL,
    source         VARCHAR(20)    NOT NULL DEFAULT 'STONE',
    raw_payload    JSONB,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_pix_txid_tenant UNIQUE (tenant_id, txid),
    CONSTRAINT chk_pix_source CHECK (source IN ('STONE', 'WEBHOOK', 'SYNC'))
);

CREATE INDEX idx_pix_transactions_tenant_id ON pix_transactions (tenant_id);
CREATE INDEX idx_pix_transactions_received_at ON pix_transactions (received_at);
CREATE INDEX idx_pix_transactions_end_to_end_id ON pix_transactions (end_to_end_id);

-- Vínculo PIX com contas a receber
ALTER TABLE account_receivables
    ADD COLUMN pix_txid VARCHAR(35);

CREATE INDEX idx_receivables_pix_txid ON account_receivables (pix_txid);

-- Conciliações
CREATE TABLE reconciliations (
    id                 UUID PRIMARY KEY,
    tenant_id          UUID           NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    pix_transaction_id UUID           NOT NULL REFERENCES pix_transactions (id) ON DELETE CASCADE,
    receivable_id      UUID           REFERENCES account_receivables (id),
    expected_amount    NUMERIC(19, 2),
    received_amount    NUMERIC(19, 2) NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    reconciled_at      TIMESTAMPTZ,
    notes              VARCHAR(500),
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_reconciliation_status CHECK (status IN ('MATCHED', 'DIVERGENT', 'PENDING')),
    CONSTRAINT uk_reconciliation_pix UNIQUE (pix_transaction_id)
);

CREATE INDEX idx_reconciliations_tenant_id ON reconciliations (tenant_id);
CREATE INDEX idx_reconciliations_status ON reconciliations (status);
CREATE INDEX idx_reconciliations_receivable_id ON reconciliations (receivable_id);

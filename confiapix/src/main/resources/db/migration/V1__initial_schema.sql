-- Tenants
CREATE TABLE tenants (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    plan        VARCHAR(50)  NOT NULL DEFAULT 'FREE',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tenants_active ON tenants (active);

-- Users
CREATE TABLE users (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    tenant_id   UUID         NOT NULL REFERENCES tenants (id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_tenant_id ON users (tenant_id);

-- Companies
CREATE TABLE companies (
    id              UUID PRIMARY KEY,
    corporate_name  VARCHAR(255) NOT NULL,
    trade_name      VARCHAR(255) NOT NULL,
    document        VARCHAR(20)  NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    tenant_id       UUID         NOT NULL REFERENCES tenants (id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_companies_tenant_id ON companies (tenant_id);
CREATE INDEX idx_companies_document ON companies (document);

-- Customers
CREATE TABLE customers (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    document    VARCHAR(20),
    email       VARCHAR(255),
    phone       VARCHAR(30),
    company_id  UUID         NOT NULL REFERENCES companies (id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customers_company_id ON customers (company_id);

-- Suppliers
CREATE TABLE suppliers (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    document    VARCHAR(20),
    email       VARCHAR(255),
    phone       VARCHAR(30),
    company_id  UUID         NOT NULL REFERENCES companies (id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_suppliers_company_id ON suppliers (company_id);

-- Account Receivables
CREATE TABLE account_receivables (
    id           UUID PRIMARY KEY,
    description  VARCHAR(500)   NOT NULL,
    amount       NUMERIC(19, 2) NOT NULL,
    due_date     DATE           NOT NULL,
    payment_date DATE,
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    customer_id  UUID           NOT NULL REFERENCES customers (id),
    company_id   UUID           NOT NULL REFERENCES companies (id),
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_receivable_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELED'))
);

CREATE INDEX idx_receivables_company_id ON account_receivables (company_id);
CREATE INDEX idx_receivables_due_date ON account_receivables (due_date);
CREATE INDEX idx_receivables_status ON account_receivables (status);

-- Account Payables
CREATE TABLE account_payables (
    id           UUID PRIMARY KEY,
    description  VARCHAR(500)   NOT NULL,
    amount       NUMERIC(19, 2) NOT NULL,
    due_date     DATE           NOT NULL,
    payment_date DATE,
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    supplier_id  UUID           NOT NULL REFERENCES suppliers (id),
    company_id   UUID           NOT NULL REFERENCES companies (id),
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_payable_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELED'))
);

CREATE INDEX idx_payables_company_id ON account_payables (company_id);
CREATE INDEX idx_payables_due_date ON account_payables (due_date);
CREATE INDEX idx_payables_status ON account_payables (status);

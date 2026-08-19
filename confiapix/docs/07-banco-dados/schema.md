# Schema PostgreSQL

**Tags:** #ConfiaPix #banco-dados #postgresql

[[README|← Voltar ao índice]]

---

## Migration

Arquivo: `src/main/resources/db/migration/V1__initial_schema.sql`

Gerenciado pelo **Flyway** com `ddl-auto: validate`.

---

## Tabelas

| Tabela | Descrição |
|--------|-----------|
| `tenants` | Organizações SaaS |
| `users` | Usuários autenticáveis |
| `companies` | Empresas por tenant |
| `customers` | Clientes por empresa |
| `suppliers` | Fornecedores por empresa |
| `account_receivables` | Contas a receber |
| `account_payables` | Contas a pagar |

---

## Diagrama relacional

> **Diagrama Canvas:** [[canvas/schema-banco.canvas|Schema do Banco]] · [[canvas/modelo-dominio-er.canvas|Modelo ER]]

---

## Índices

| Tabela | Índice | Coluna(s) |
|--------|--------|-----------|
| tenants | idx_tenants_active | active |
| users | idx_users_tenant_id | tenant_id |
| companies | idx_companies_tenant_id | tenant_id |
| companies | idx_companies_document | document |
| customers | idx_customers_company_id | company_id |
| suppliers | idx_suppliers_company_id | company_id |
| account_receivables | idx_receivables_company_id | company_id |
| account_receivables | idx_receivables_due_date | due_date |
| account_receivables | idx_receivables_status | status |
| account_payables | idx_payables_company_id | company_id |
| account_payables | idx_payables_due_date | due_date |
| account_payables | idx_payables_status | status |

---

## Constraints

### users

- `uk_users_email` — e-mail único global

### account_receivables / account_payables

- `chk_receivable_status` / `chk_payable_status` — status IN (`PENDING`, `PAID`, `OVERDUE`, `CANCELED`)

---

## Tipos de dados

| Campo | Tipo PostgreSQL |
|-------|-----------------|
| id | UUID |
| amount | NUMERIC(19, 2) |
| due_date / payment_date | DATE |
| created_at / updated_at | TIMESTAMPTZ |
| status | VARCHAR(20) |

---

## Chaves primárias

Todas as tabelas usam **UUID** como PK, gerado pela aplicação (`GenerationType.UUID`).

---

## Documentos relacionados

- [[03-dominio/modelo-de-dominio|Modelo de Domínio]]
- [[07-banco-dados/migrations|Flyway Migrations]]

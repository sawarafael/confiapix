# Modelo de Domínio

**Tags:** #ConfiaPix #dominio #entidades

[[README|← Voltar ao índice]]

---

## Diagrama entidade-relacionamento

> **Diagrama Canvas:** [[canvas/modelo-dominio-er.canvas|Modelo ER]]

---

## Entidades

Detalhamento completo em [[03-dominio/entidades|Entidades]].

| Entidade | Tabela | Pertence a |
|----------|--------|------------|
| Tenant | `tenants` | — (raiz SaaS) |
| User | `users` | Tenant |
| Company | `companies` | Tenant |
| Customer | `customers` | Company |
| Supplier | `suppliers` | Company |
| AccountReceivable | `account_receivables` | Company + Customer |
| AccountPayable | `account_payables` | Company + Supplier |

---

## Enum: AccountStatus

Usado em contas a receber e a pagar:

| Status | Significado |
|--------|-------------|
| `PENDING` | Em aberto, dentro do prazo |
| `OVERDUE` | Em aberto, vencido |
| `PAID` | Quitado |
| `CANCELED` | Cancelado |

Ver [[04-regras-negocio/status-contas|Regras de Status de Contas]].

---

## BaseEntity (auditoria)

Todas as entidades herdam:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Chave primária gerada automaticamente |
| `createdAt` | Instant | Preenchido na criação |
| `updatedAt` | Instant | Atualizado em cada modificação |

---

## Documentos relacionados

- [[03-dominio/entidades|Entidades — detalhamento]]
- [[07-banco-dados/schema|Schema PostgreSQL]]

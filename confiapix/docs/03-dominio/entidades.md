# Entidades — Detalhamento

**Tags:** #ConfiaPix #dominio #entidades

[[03-dominio/modelo-de-dominio|← Modelo de Domínio]]

---

## Tenant

Organização cliente do SaaS. Criada no registro.

| Campo | Obrigatório | Descrição |
|-------|-------------|-----------|
| `name` | Sim | Nome da organização |
| `plan` | Sim | Plano contratado (padrão: `FREE`) |
| `active` | Sim | Se o tenant está ativo (padrão: `true`) |

---

## User

Usuário autenticável. Pertence a exatamente um tenant.

| Campo | Obrigatório | Descrição |
|-------|-------------|-----------|
| `name` | Sim | Nome completo |
| `email` | Sim | E-mail único globalmente |
| `password` | Sim | Hash BCrypt |
| `active` | Sim | Se pode autenticar |
| `tenant` | Sim | Tenant proprietário |

---

## Company

Empresa operacional dentro do tenant. Suporta **multi-empresa**.

| Campo | Obrigatório | Descrição |
|-------|-------------|-----------|
| `corporateName` | Sim | Razão social |
| `tradeName` | Sim | Nome fantasia |
| `document` | Sim | CNPJ ou documento fiscal |
| `active` | Sim | Empresa ativa |
| `tenant` | Sim | Tenant proprietário |

---

## Customer

Cliente final de uma empresa.

| Campo | Obrigatório | Descrição |
|-------|-------------|-----------|
| `name` | Sim | Nome ou razão social |
| `document` | Não | CPF/CNPJ |
| `email` | Não | Contato |
| `phone` | Não | Telefone |
| `company` | Sim | Empresa à qual pertence |

---

## Supplier

Fornecedor de uma empresa. Mesma estrutura de Customer.

---

## AccountReceivable

Título financeiro a receber de um cliente.

| Campo | Obrigatório | Descrição |
|-------|-------------|-----------|
| `description` | Sim | Descrição do título |
| `amount` | Sim | Valor (> 0) |
| `dueDate` | Sim | Data de vencimento |
| `paymentDate` | Não | Preenchido na baixa |
| `status` | Sim | Ver enum AccountStatus |
| `customer` | Sim | Cliente devedor |
| `company` | Sim | Empresa credora |

---

## AccountPayable

Título financeiro a pagar a um fornecedor. Mesma estrutura de AccountReceivable, substituindo `customer` por `supplier`.

---

## Invariantes de domínio

> **Diagrama Canvas:** [[canvas/invariantes-dominio.canvas|Invariantes de Domínio]]

Ver [[04-regras-negocio/indice-regras|Regras de Negócio]].

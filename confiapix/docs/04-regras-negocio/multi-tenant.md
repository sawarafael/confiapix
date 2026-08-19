# Regras Multi-Tenant

**Tags:** #ConfiaPix #regra-negocio #multi-tenant

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## Estratégia: Shared Database

Todos os tenants compartilham o **mesmo banco PostgreSQL**, isolados por coluna `tenant_id` ou cadeia de relacionamentos.

> **Diagrama Canvas:** [[canvas/multi-tenant-isolamento.canvas|Shared Database + Isolamento]]

---

## Regras

### RN-MT01 — Criação de tenant no registro

> O endpoint `POST /auth/register` cria **simultaneamente** um Tenant e o primeiro User admin vinculado a ele.

### RN-MT02 — Tenant no JWT

> O token JWT contém claims `tenantId` e `userId`. O e-mail do usuário é o `subject`.

### RN-MT03 — Contexto por requisição

> `TenantContextHolder` armazena o tenant da requisição atual em `ThreadLocal`, populado pelo `JwtAuthenticationFilter` e limpo no `finally` do filtro.

### RN-MT04 — Filtro em Company e User

> `Company` possui `tenant_id` direto. Repositórios usam `findByTenantId`.

### RN-MT05 — Filtro indireto em Customer, Supplier e Contas

> Entidades filhas filtram via `company.tenant.id` em queries JPQL:

```sql
-- Exemplo lógico
SELECT c FROM Customer c WHERE c.company.tenant.id = :tenantId
```

### RN-MT06 — Validação de pertencimento

> Ao criar/atualizar Customer, Supplier ou Conta, o service valida que a `companyId` informada pertence ao tenant atual via `CompanyService.getCompanyOrThrow()`.

### RN-MT07 — Acesso cross-tenant impossível

> Tentativa de acessar recurso de outro tenant retorna **404** (não 403), evitando vazamento de existência de IDs.

---

## Fluxo de isolamento

> **Diagrama Canvas:** [[canvas/multi-tenant-isolamento.canvas|Fluxo de Isolamento]]

---

## Documentos relacionados

- [[08-seguranca/jwt-e-autenticacao|JWT e Autenticação]]
- [[05-fluxos/requisicao-autenticada|Fluxo de Requisição Autenticada]]

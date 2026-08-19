# Índice de Regras de Negócio

**Tags:** #ConfiaPix #regra-negocio

[[README|← Voltar ao índice]]

---

## Regras por módulo

| Módulo | Documento |
|--------|-----------|
| Multi-tenant e isolamento | [[04-regras-negocio/multi-tenant|Multi-Tenant]] |
| Autenticação e registro | [[04-regras-negocio/autenticacao|Autenticação]] |
| Empresas | [[04-regras-negocio/empresas|Empresas]] |
| Clientes e fornecedores | [[04-regras-negocio/parceiros|Parceiros]] |
| Status de contas | [[04-regras-negocio/status-contas|Status de Contas]] |
| Contas a receber | [[04-regras-negocio/contas-receber|Contas a Receber]] |
| Contas a pagar | [[04-regras-negocio/contas-pagar|Contas a Pagar]] |
| Dashboard | [[04-regras-negocio/dashboard|Dashboard]] |

---

## Diagramas Canvas (regras visuais)

| Diagrama | Canvas |
|----------|--------|
| Multi-tenant | [[canvas/multi-tenant-isolamento.canvas]] |
| Status de contas | [[canvas/status-contas-estados.canvas]] |
| Dashboard (cálculo) | [[canvas/fluxo-dashboard.canvas]] |
| Invariantes | [[canvas/invariantes-dominio.canvas]] |
| Índice completo | [[canvas/indice-diagramas.canvas]] |

---

## Regras transversais

### RN-G01 — Isolamento de tenant

> Nenhum tenant pode visualizar, alterar ou excluir dados de outro tenant.

Implementação: filtro em repositórios via `TenantContextHolder.getTenantId()`.

### RN-G02 — Autenticação obrigatória

> Todos os endpoints, exceto registro, login e documentação Swagger, exigem JWT válido.

### RN-G03 — Resposta padronizada

> Toda resposta de sucesso usa `ApiResponse`. Erros são tratados pelo `GlobalExceptionHandler`.

### RN-G04 — Paginação obrigatória

> Endpoints de listagem (`GET` coleções) retornam `PageResponse` com paginação Spring Data.

Parâmetros: `page` (0-based), `size` (padrão 20), `sort`.

### RN-G05 — Auditoria automática

> `createdAt` e `updatedAt` são gerenciados pelo JPA Auditing, nunca enviados pelo cliente.

### RN-G06 — Identificadores UUID

> Todas as chaves primárias são UUID v4 gerados pelo banco/aplicação.

---

## Matriz de validações de entrada

| Campo | Validação |
|-------|-----------|
| E-mail | Formato válido (`@Email`) |
| Senha (registro) | Mínimo 6 caracteres |
| Valor financeiro | `@DecimalMin("0.01")` |
| Descrição | `@NotBlank` |
| IDs de relacionamento | `@NotNull` + validação de pertencimento no service |

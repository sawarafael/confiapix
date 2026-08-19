# Fluxo de Onboarding

**Tags:** #ConfiaPix #fluxo #onboarding

[[05-fluxos/indice-fluxos|← Índice de Fluxos]]

---

## Objetivo

Guiar um novo cliente SaaS desde o registro até a primeira consulta no dashboard.

---

## Sequência

> **Diagrama Canvas:** [[canvas/fluxo-onboarding.canvas|Fluxo de Onboarding]]

---

## Passo a passo

### 1. Registrar organização

```http
POST /auth/register
Content-Type: application/json

{
  "tenantName": "Acme Financeiro",
  "name": "João Silva",
  "email": "joao@acme.com",
  "password": "senha123"
}
```

Guarde o `token` retornado.

### 2. Criar empresa

```http
POST /companies
Authorization: Bearer {token}

{
  "corporateName": "Acme LTDA",
  "tradeName": "Acme",
  "document": "12345678000199"
}
```

### 3. Cadastrar cliente

```http
POST /customers
Authorization: Bearer {token}

{
  "name": "Cliente XYZ",
  "document": "12345678901",
  "email": "xyz@email.com",
  "phone": "11999999999",
  "companyId": "{companyId}"
}
```

### 4. Cadastrar fornecedor

Mesma estrutura em `POST /suppliers`.

### 5. Criar títulos

```http
POST /receivables
POST /payables
```

### 6. Consultar dashboard

```http
GET /dashboard
Authorization: Bearer {token}
```

---

## Documentos relacionados

- [[06-api/indice-api|Referência da API]]
- [[04-regras-negocio/autenticacao|Regras de Autenticação]]

# API — Customers

**Tags:** #ConfiaPix #api #customer

[[06-api/indice-api|← Referência da API]]

---

## GET /customers

Lista clientes do tenant (via empresa). Paginado.

**Sort padrão:** `createdAt,desc`

---

## GET /customers/{id}

---

## POST /customers

### Request

```json
{
  "name": "Cliente XYZ",
  "document": "12345678901",
  "email": "cliente@email.com",
  "phone": "11999999999",
  "companyId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| Campo | Obrigatório |
|-------|-------------|
| name | Sim |
| companyId | Sim |
| document | Não |
| email | Não (validado se informado) |
| phone | Não |

### Response — 201

```json
{
  "success": true,
  "message": "Cliente criado com sucesso",
  "data": {
    "id": "uuid",
    "name": "Cliente XYZ",
    "document": "12345678901",
    "email": "cliente@email.com",
    "phone": "11999999999",
    "companyId": "uuid",
    "companyName": "Acme",
    "createdAt": "2026-06-04T10:00:00Z"
  }
}
```

---

## PUT /customers/{id}

Mesmo body do POST.

---

## DELETE /customers/{id}

Response — 204.

# API — Suppliers

**Tags:** #ConfiaPix #api #supplier

[[06-api/indice-api|← Referência da API]]

---

Estrutura idêntica a [[06-api/customers|Customers]], com endpoints `/suppliers`.

## POST /suppliers

### Request

```json
{
  "name": "Fornecedor ABC",
  "document": "98765432000199",
  "email": "fornecedor@email.com",
  "phone": "11888888888",
  "companyId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Response — 201

```json
{
  "success": true,
  "message": "Fornecedor criado com sucesso",
  "data": {
    "id": "uuid",
    "name": "Fornecedor ABC",
    "document": "98765432000199",
    "email": "fornecedor@email.com",
    "phone": "11888888888",
    "companyId": "uuid",
    "companyName": "Acme",
    "createdAt": "2026-06-04T10:00:00Z"
  }
}
```

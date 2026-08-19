# API — Companies

**Tags:** #ConfiaPix #api #company

[[06-api/indice-api|← Referência da API]]

---

## GET /companies

Lista empresas do tenant. Paginado.

**Sort padrão:** `createdAt,desc`

### Response — 200

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "uuid",
        "corporateName": "Acme LTDA",
        "tradeName": "Acme",
        "document": "12345678000199",
        "active": true,
        "createdAt": "2026-06-04T10:00:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

## GET /companies/{id}

Retorna uma empresa por ID.

---

## POST /companies

### Request

```json
{
  "corporateName": "Acme LTDA",
  "tradeName": "Acme",
  "document": "12345678000199",
  "active": true
}
```

### Response — 201

---

## PUT /companies/{id}

Atualiza empresa existente. Mesmo body do POST.

---

## DELETE /companies/{id}

Exclui empresa. Response — 204 (sem body).

---

## Erros comuns

| HTTP | Motivo |
|------|--------|
| 404 | Empresa não encontrada ou de outro tenant |
| 400 | Validação de campos |

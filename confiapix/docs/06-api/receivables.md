# API — Receivables

**Tags:** #ConfiaPix #api #receivable

[[06-api/indice-api|← Referência da API]]

---

## GET /receivables

Lista contas a receber do tenant. Paginado.

**Sort padrão:** `dueDate,asc`

---

## GET /receivables/{id}

---

## POST /receivables

### Request

```json
{
  "description": "Venda de serviços — Junho",
  "amount": 1500.00,
  "dueDate": "2026-06-15",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "companyId": "550e8400-e29b-41d4-a716-446655440001"
}
```

| Campo | Obrigatório | Validação |
|-------|-------------|-----------|
| description | Sim | NotBlank |
| amount | Sim | >= 0.01 |
| dueDate | Sim | ISO date |
| customerId | Sim | UUID válido do tenant |
| companyId | Sim | UUID válido do tenant |

### Response — 201

```json
{
  "success": true,
  "message": "Conta a receber criada com sucesso",
  "data": {
    "id": "uuid",
    "description": "Venda de serviços — Junho",
    "amount": 1500.00,
    "dueDate": "2026-06-15",
    "paymentDate": null,
    "status": "PENDING",
    "customerId": "uuid",
    "customerName": "Cliente XYZ",
    "companyId": "uuid",
    "companyName": "Acme",
    "createdAt": "2026-06-04T10:00:00Z"
  }
}
```

---

## PUT /receivables/{id}

Mesmo body do POST. **Não permitido** se status = `PAID`.

---

## PATCH /receivables/{id}/pay

Registra baixa do título. Sem body.

### Response — 200

Retorna conta com `status: "PAID"` e `paymentDate` preenchido.

### Erros

| HTTP | Motivo |
|------|--------|
| 400 | Já paga ou cancelada |
| 404 | Não encontrada |

---

## DELETE /receivables/{id}

Response — 204.

---

## Documentos relacionados

- [[04-regras-negocio/contas-receber|Regras]]
- [[05-fluxos/conta-receber|Fluxo]]

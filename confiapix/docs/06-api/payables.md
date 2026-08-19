# API — Payables

**Tags:** #ConfiaPix #api #payable

[[06-api/indice-api|← Referência da API]]

---

Estrutura idêntica a [[06-api/receivables|Receivables]], com endpoints `/payables` e `supplierId` no lugar de `customerId`.

## POST /payables

### Request

```json
{
  "description": "Aluguel — Junho",
  "amount": 3500.00,
  "dueDate": "2026-06-10",
  "supplierId": "550e8400-e29b-41d4-a716-446655440000",
  "companyId": "550e8400-e29b-41d4-a716-446655440001"
}
```

---

## PATCH /payables/{id}/pay

Baixa de conta a pagar. Sem body.

---

## Documentos relacionados

- [[04-regras-negocio/contas-pagar|Regras]]
- [[05-fluxos/conta-pagar|Fluxo]]

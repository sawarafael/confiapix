# API — Dashboard

**Tags:** #ConfiaPix #api #dashboard

[[06-api/indice-api|← Referência da API]]

---

## GET /dashboard

Retorna indicadores financeiros consolidados do tenant.

**Auth:** Sim

### Response — 200

```json
{
  "success": true,
  "data": {
    "totalReceivable": 15000.00,
    "totalPayable": 8500.00,
    "overdueReceivable": 3200.00,
    "overduePayable": 1100.00,
    "projectedBalance": 6500.00
  }
}
```

| Campo | Descrição |
|-------|-----------|
| totalReceivable | Soma de títulos a receber em aberto |
| totalPayable | Soma de títulos a pagar em aberto |
| overdueReceivable | Soma de títulos a receber vencidos |
| overduePayable | Soma de títulos a pagar vencidos |
| projectedBalance | totalReceivable − totalPayable |

Ver [[04-regras-negocio/dashboard|Regras do Dashboard]].

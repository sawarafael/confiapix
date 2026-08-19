# Regras — Dashboard

**Tags:** #ConfiaPix #regra-negocio #dashboard

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## Endpoint

`GET /dashboard` — retorna indicadores **consolidados por tenant** (todas as empresas).

---

## Métricas

### RN-DB01 — totalReceivable

> Soma dos valores de contas a receber com status `PENDING` **ou** `OVERDUE`.

### RN-DB02 — totalPayable

> Soma dos valores de contas a pagar com status `PENDING` **ou** `OVERDUE`.

### RN-DB03 — overdueReceivable

> Soma de contas a receber vencidas, calculada como:
> - Contas com status `OVERDUE` persistido, **mais**
> - Contas `PENDING` com `dueDate < hoje`

### RN-DB04 — overduePayable

> Mesma lógica de RN-DB03 para contas a pagar.

### RN-DB05 — projectedBalance

```
projectedBalance = totalReceivable - totalPayable
```

> Representa o saldo projetado considerando apenas títulos em aberto (pendentes + vencidos). Títulos pagos ou cancelados **não entram** no cálculo.

---

## Diagrama de cálculo

> **Diagrama Canvas:** [[canvas/fluxo-dashboard.canvas|Cálculo do Dashboard]]

---

## Exemplo de resposta

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

---

## Documentos relacionados

- [[06-api/dashboard|API — Dashboard]]
- [[05-fluxos/dashboard|Fluxo do Dashboard]]

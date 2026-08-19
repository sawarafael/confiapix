# Regras — Contas a Pagar

**Tags:** #ConfiaPix #regra-negocio #payable

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

As regras de contas a pagar são **simétricas** às de contas a receber, substituindo `customer` por `supplier`.

## RN-CP01 — Criação

| Regra | Descrição |
|-------|-----------|
| RN-CP01a | `description`, `amount`, `dueDate`, `supplierId` e `companyId` obrigatórios |
| RN-CP01b | Fornecedor deve pertencer à mesma empresa informada |
| RN-CP01c | Status inicial via [[04-regras-negocio/status-contas|AccountStatusHelper]] |

## RN-CP02 — Atualização

> Contas `PAID` não podem ser alteradas.

## RN-CP03 — Baixa

Endpoint: `PATCH /payables/{id}/pay`

| Regra | Descrição |
|-------|-----------|
| RN-CP03a | Já paga → erro 400 |
| RN-CP03b | Cancelada → erro 400 |
| RN-CP03c | Status → `PAID`, `paymentDate` = hoje |

## RN-CP04 — Ordenação

> Listagem por `dueDate` ascendente.

---

## Documentos relacionados

- [[06-api/payables|API — Payables]]
- [[05-fluxos/conta-pagar|Fluxo Conta a Pagar]]

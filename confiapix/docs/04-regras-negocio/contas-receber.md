# Regras — Contas a Receber

**Tags:** #ConfiaPix #regra-negocio #receivable

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## RN-CR01 — Criação

| Regra | Descrição |
|-------|-----------|
| RN-CR01a | `description`, `amount`, `dueDate`, `customerId` e `companyId` são obrigatórios |
| RN-CR01b | `amount` deve ser > 0 |
| RN-CR01c | Cliente deve pertencer à mesma empresa informada |
| RN-CR01d | Empresa deve pertencer ao tenant autenticado |
| RN-CR01e | Status inicial calculado por [[04-regras-negocio/status-contas|AccountStatusHelper]] |

## RN-CR02 — Atualização

| Regra | Descrição |
|-------|-----------|
| RN-CR02a | Contas com status `PAID` **não podem ser alteradas** |
| RN-CR02b | Demais validações de criação se aplicam |

## RN-CR03 — Baixa (pagamento)

Endpoint: `PATCH /receivables/{id}/pay`

| Regra | Descrição |
|-------|-----------|
| RN-CR03a | Conta já `PAID` → erro 400 |
| RN-CR03b | Conta `CANCELED` → erro 400 |
| RN-CR03c | Status alterado para `PAID` |
| RN-CR03d | `paymentDate` preenchido com data atual |

## RN-CR04 — Exclusão

> Hard delete permitido para contas do tenant. Sem restrição por status no MVP.

## RN-CR05 — Ordenação padrão

> Listagem ordenada por `dueDate` ascendente (vencimentos mais próximos primeiro).

---

## Fluxo resumido

> **Diagrama Canvas:** [[canvas/fluxo-conta-receber.canvas|Fluxo Conta a Receber]]

Ver [[05-fluxos/conta-receber|Fluxo completo]].

---

## Documentos relacionados

- [[06-api/receivables|API — Receivables]]
- [[04-regras-negocio/dashboard|Dashboard]]

# Regras — Status de Contas

**Tags:** #ConfiaPix #regra-negocio #status

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## Estados possíveis

> **Diagrama Canvas:** [[canvas/status-contas-estados.canvas|Estados de Contas]]

> **Nota MVP:** transição para `CANCELED` está prevista no enum e no banco, mas não há endpoint dedicado no MVP 1.0.

---

## RN-ST01 — Resolução dinâmica de status

> Status `PENDING` com `dueDate` anterior à data atual é **exibido como `OVERDUE`** na leitura, via `AccountStatusHelper.resolveStatus()`.

Lógica:

```
SE status == PAID ou CANCELED → mantém
SENÃO SE dueDate < hoje       → OVERDUE
SENÃO                         → PENDING
```

### RN-ST02 — Status terminais

> `PAID` e `CANCELED` **nunca** são alterados automaticamente pelo helper.

### RN-ST03 — Persistência vs exibição

> Na criação/atualização, o status é calculado e **persistido**. Na leitura, o helper recalcula para exibição consistente mesmo sem job agendado.

---

## Documentos relacionados

- [[04-regras-negocio/contas-receber|Contas a Receber]]
- [[04-regras-negocio/contas-pagar|Contas a Pagar]]
- [[05-fluxos/conta-receber|Fluxo Conta a Receber]]

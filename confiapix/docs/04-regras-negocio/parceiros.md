# Regras — Parceiros (Clientes e Fornecedores)

**Tags:** #ConfiaPix #regra-negocio #cliente #fornecedor

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## Regras comuns (Customer e Supplier)

### RN-PAR01 — Vínculo com empresa

> Todo parceiro deve estar vinculado a uma `companyId` válida pertencente ao tenant.

### RN-PAR02 — Nome obrigatório

> Campo `name` é obrigatório.

### RN-PAR03 — Campos opcionais

> `document`, `email` e `phone` são opcionais. Se `email` informado, deve ser válido.

### RN-PAR04 — Isolamento

> Listagem e consulta filtram indiretamente pelo tenant via `company.tenant.id`.

### RN-PAR05 — Atualização de empresa

> Ao alterar `companyId`, a nova empresa também deve pertencer ao tenant.

---

## Diferenças

| Aspecto | Customer | Supplier |
|---------|----------|----------|
| Usado em | Contas a receber | Contas a pagar |
| Endpoint | `/customers` | `/suppliers` |

---

## Documentos relacionados

- [[06-api/customers|API — Customers]]
- [[06-api/suppliers|API — Suppliers]]

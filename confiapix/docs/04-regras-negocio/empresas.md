# Regras — Empresas

**Tags:** #ConfiaPix #regra-negocio #empresa

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## RN-EMP01 — Criação

> Empresa é sempre criada no tenant do usuário autenticado. O cliente **não informa** `tenantId`.

## RN-EMP02 — Campos obrigatórios

- Razão social (`corporateName`)
- Nome fantasia (`tradeName`)
- Documento (`document`)

## RN-EMP03 — Listagem e consulta

> Apenas empresas do tenant atual são retornadas.

## RN-EMP04 — Atualização

> Só é possível atualizar empresas pertencentes ao tenant. Caso contrário: 404.

## RN-EMP05 — Exclusão

> Exclusão física (hard delete). **Atenção:** em produção futura, considerar soft delete ou restrição se houver contas vinculadas.

## RN-EMP06 — Multi-empresa

> Um tenant pode ter **N empresas**. Clientes, fornecedores e títulos são sempre vinculados a uma empresa específica.

---

## Documentos relacionados

- [[06-api/companies|API — Companies]]
- [[04-regras-negocio/parceiros|Parceiros (clientes/fornecedores)]]

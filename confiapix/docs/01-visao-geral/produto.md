# Visão do Produto

**Tags:** #ConfiaPix #produto #mvp

[[README|← Voltar ao índice]]

---

## Problema

Pequenas e médias empresas precisam de um sistema simples para controlar entradas e saídas financeiras, sem a complexidade de um ERP completo.

## Solução

O ConfiaPix oferece:

| Funcionalidade | Descrição |
|----------------|-----------|
| Cadastro de empresas | Uma ou mais empresas por tenant |
| Cadastro de clientes | Vinculados a uma empresa |
| Cadastro de fornecedores | Vinculados a uma empresa |
| Contas a receber | Títulos de clientes com vencimento e pagamento |
| Contas a pagar | Títulos de fornecedores com vencimento e pagamento |
| Dashboard | Totais, vencidos e saldo projetado |
| Multi-tenant | Isolamento total entre organizações SaaS |

---

## Escopo do MVP 1.0

### Incluído

- Registro de tenant + usuário admin
- Login com JWT
- CRUD de empresas, clientes, fornecedores
- CRUD de contas a receber/pagar
- Baixa de título (`PATCH .../pay`)
- Dashboard consolidado por tenant
- Paginação em todas as listagens
- Validação de entrada (Bean Validation)
- Auditoria `createdAt` / `updatedAt`

### Fora do escopo (MVP)

- Emissão de NF-e / boletos
- Conciliação bancária
- Relatórios contábeis (DRE, balancete)
- Múltiplos usuários por tenant com perfis (RBAC)
- Planos pagos e billing
- Notificações por e-mail
- Job agendado para marcar vencidos no banco

---

## Personas

### Administrador do Tenant

- Registra a organização no SaaS
- Cadastra empresas (CNPJ/razão social)
- Gerencia clientes, fornecedores e títulos
- Consulta o dashboard

### Sistema (integração futura)

- Consumirá a API REST com token JWT
- Front-end web/mobile (fora deste repositório)

---

## Hierarquia de dados

> **Diagrama Canvas:** [[canvas/hierarquia-dados.canvas|Hierarquia de Dados]]

---

## Documentos relacionados

- [[03-dominio/modelo-de-dominio|Modelo de Domínio]]
- [[04-regras-negocio/indice-regras|Regras de Negócio]]
- [[05-fluxos/onboarding|Fluxo de Onboarding]]

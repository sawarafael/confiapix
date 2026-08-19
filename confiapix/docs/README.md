# ConfiaPix — Documentação

> Plataforma SaaS de gestão financeira para PMEs. MVP 1.0 — Backend.

**Tags:** #ConfiaPix #documentacao #mvp

---

## Navegação rápida

| Área | Documento |
|------|-----------|
| Visão do produto | [[01-visao-geral/produto|Produto]] |
| Arquitetura | [[02-arquitetura/arquitetura-geral|Arquitetura Geral]] |
| Modelo de domínio | [[03-dominio/modelo-de-dominio|Modelo de Domínio]] |
| Regras de negócio | [[04-regras-negocio/indice-regras|Índice de Regras]] |
| Fluxos | [[05-fluxos/indice-fluxos|Índice de Fluxos]] |
| API REST | [[06-api/indice-api|Referência da API]] |
| Banco de dados | [[07-banco-dados/schema|Schema PostgreSQL]] |
| Segurança | [[08-seguranca/jwt-e-autenticacao|JWT e Autenticação]] |
| Operação | [[09-operacao/configuracao-e-deploy|Configuração e Deploy]] |
| **Diagramas Canvas** | [[canvas/indice-diagramas.canvas|Índice de Diagramas]] |

---

## O que é o ConfiaPix?

O ConfiaPix **não é um ERP completo**. O foco é **controle financeiro** para pequenas e médias empresas, com suporte a:

- Multi-tenant (vários clientes SaaS isolados)
- Multi-empresa (várias empresas dentro de um tenant)
- Contas a receber e a pagar
- Dashboard financeiro consolidado

---

## Stack

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4 |
| Segurança | Spring Security + JWT |
| Banco | PostgreSQL 16 |
| Migrations | Flyway |
| Mapeamento | MapStruct |
| Documentação API | OpenAPI / Swagger |
| Build | Maven |

---

## Mapa mental do sistema

> **Diagrama Canvas:** [[canvas/mapa-sistema.canvas|Mapa do Sistema]]

---

## Primeiros passos

1. Leia [[09-operacao/configuracao-e-deploy|Configuração e Deploy]]
2. Registre um tenant via `POST /auth/register`
3. Autentique com `POST /auth/login`
4. Siga o [[05-fluxos/onboarding|Fluxo de Onboarding]]

---

## Links externos

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

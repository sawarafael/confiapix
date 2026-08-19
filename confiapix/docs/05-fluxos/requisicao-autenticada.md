# Fluxo de Requisição Autenticada

**Tags:** #ConfiaPix #fluxo #security #multi-tenant

[[05-fluxos/indice-fluxos|← Índice de Fluxos]]

---

## Pipeline completo

> **Diagrama Canvas:** [[canvas/fluxo-requisicao-jwt.canvas|Pipeline de Requisição JWT]]

---

## Endpoints sem autenticação

| Path | Motivo |
|------|--------|
| `POST /auth/register` | Criação de conta |
| `POST /auth/login` | Obtenção de token |
| `/swagger-ui/**` | Documentação |
| `/api-docs/**` | OpenAPI spec |

---

## Códigos de resposta de segurança

| Código | Situação |
|--------|----------|
| 401 | Token ausente, inválido ou expirado |
| 403 | Autenticado mas sem permissão (raro no MVP) |
| 404 | Recurso não encontrado ou de outro tenant |

---

## Documentos relacionados

- [[04-regras-negocio/multi-tenant|Regras Multi-Tenant]]
- [[08-seguranca/jwt-e-autenticacao|JWT e Autenticação]]

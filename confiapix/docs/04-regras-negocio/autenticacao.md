# Regras de Autenticação

**Tags:** #ConfiaPix #regra-negocio #auth

[[04-regras-negocio/indice-regras|← Índice de Regras]]

---

## Registro — RN-AUTH01

| Regra | Descrição |
|-------|-----------|
| RN-AUTH01a | E-mail deve ser único em todo o sistema |
| RN-AUTH01b | Senha é armazenada com BCrypt (nunca em texto plano) |
| RN-AUTH01c | Tenant criado com plano `FREE` e `active = true` |
| RN-AUTH01d | User criado com `active = true` |
| RN-AUTH01e | Resposta inclui JWT imediatamente após registro |

---

## Login — RN-AUTH02

| Regra | Descrição |
|-------|-----------|
| RN-AUTH02a | Credenciais inválidas retornam 401 |
| RN-AUTH02b | Usuário inativo (`active = false`) retorna 400 com mensagem |
| RN-AUTH02c | JWT expira conforme `ConfiaPix.jwt.expiration-ms` (padrão: 24h) |

---

## Token JWT — RN-AUTH03

Claims incluídos:

| Claim | Descrição |
|-------|-----------|
| `sub` | E-mail do usuário |
| `tenantId` | UUID do tenant |
| `userId` | UUID do usuário |
| `iat` | Data de emissão |
| `exp` | Data de expiração |

---

## Endpoints públicos — RN-AUTH04

Sem autenticação:

- `POST /auth/register`
- `POST /auth/login`
- `/swagger-ui/**`
- `/api-docs/**`

Todos os demais exigem header:

```
Authorization: Bearer {token}
```

---

## Documentos relacionados

- [[05-fluxos/autenticacao|Fluxo de Autenticação]]
- [[06-api/auth|API — Auth]]
- [[08-seguranca/jwt-e-autenticacao|JWT — detalhes técnicos]]

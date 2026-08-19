# API — Auth

**Tags:** #ConfiaPix #api #auth

[[06-api/indice-api|← Referência da API]]

---

## POST /auth/register

Registra novo tenant e usuário administrador.

**Auth:** Não requerida

### Request

```json
{
  "tenantName": "Minha Empresa LTDA",
  "name": "Admin",
  "email": "admin@empresa.com",
  "password": "123456"
}
```

| Campo | Tipo | Obrigatório | Validação |
|-------|------|-------------|-----------|
| tenantName | string | Sim | NotBlank |
| name | string | Sim | NotBlank |
| email | string | Sim | Email válido |
| password | string | Sim | Min 6 caracteres |

### Response — 201

```json
{
  "success": true,
  "message": "Registro realizado com sucesso",
  "data": {
    "token": "eyJ...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "tenantId": "550e8400-e29b-41d4-a716-446655440001",
    "email": "admin@empresa.com",
    "name": "Admin"
  }
}
```

### Erros

| HTTP | Motivo |
|------|--------|
| 400 | E-mail já cadastrado |
| 400 | Validação de campos |

---

## POST /auth/login

Autentica usuário existente.

**Auth:** Não requerida

### Request

```json
{
  "email": "admin@empresa.com",
  "password": "123456"
}
```

### Response — 200

Mesma estrutura de `AuthResponse` do registro (sem message).

### Erros

| HTTP | Motivo |
|------|--------|
| 401 | Credenciais inválidas |
| 400 | Usuário inativo |

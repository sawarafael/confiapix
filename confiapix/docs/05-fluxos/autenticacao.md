# Fluxo de Autentica��o

**Tags:** #ConfiaPix #fluxo #auth #jwt

[[05-fluxos/indice-fluxos|? �ndice de Fluxos]]

---

## Registro

> **Diagrama Canvas:** [[canvas/fluxo-autenticacao.canvas|Fluxo de Autenticacao - Registro]]

---

## Login

> **Diagrama Canvas:** [[canvas/fluxo-autenticacao.canvas|Fluxo de Autenticacao - Login]]

---

## Estrutura do AuthResponse

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "uuid",
    "tenantId": "uuid",
    "email": "joao@acme.com",
    "name": "Jo�o Silva"
  }
}
```

---

## Uso do token

Todas as requisi��es subsequentes:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Ver [[05-fluxos/requisicao-autenticada|Fluxo de Requisi��o Autenticada]].

---

## Documentos relacionados

- [[08-seguranca/jwt-e-autenticacao|JWT ? detalhes t�cnicos]]
- [[06-api/auth|API ? Auth]]

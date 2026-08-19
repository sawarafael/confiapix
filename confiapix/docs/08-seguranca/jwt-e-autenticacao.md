# JWT e Autenticação

**Tags:** #confiapix #seguranca #jwt

[[README|← Voltar ao índice]]

---

## Componentes

| Classe | Responsabilidade |
|--------|------------------|
| `JwtService` | Gera e valida tokens |
| `JwtAuthenticationFilter` | Intercepta requests, popula contexto |
| `CustomUserDetailsService` | Carrega usuário por e-mail |
| `SecurityConfig` | Configura filter chain |

---

## Fluxo do filtro JWT

> **Diagrama Canvas:** [[canvas/fluxo-requisicao-jwt.canvas|Fluxo do Filtro JWT]]

---

## Configuração

```yaml
confiapix:
  jwt:
    secret: ${JWT_SECRET}          # Mín. 256 bits em produção
    expiration-ms: 86400000        # 24 horas
```

---

## Claims do token

```json
{
  "sub": "admin@empresa.com",
  "tenantId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "iat": 1717500000,
  "exp": 1717586400
}
```

---

## BCrypt

Senhas hasheadas com `BCryptPasswordEncoder`. O hash nunca é exposto em DTOs de resposta.

---

## Endpoints públicos (SecurityConfig)

```java
/auth/register
/auth/login
/swagger-ui/**
/swagger-ui.html
/api-docs/**
/v3/api-docs/**
```

---

## Recomendações de produção

| Item | Recomendação |
|------|--------------|
| JWT_SECRET | Variável de ambiente forte (≥ 32 bytes) |
| HTTPS | Obrigatório em produção |
| Expiração | Considerar refresh token em versões futuras |
| Rate limiting | Adicionar em `/auth/login` |

---

## Documentos relacionados

- [[04-regras-negocio/autenticacao|Regras de Autenticação]]
- [[05-fluxos/autenticacao|Fluxo de Autenticação]]
- [[09-operacao/variaveis-ambiente|Variáveis de Ambiente]]

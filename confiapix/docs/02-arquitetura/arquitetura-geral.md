# Arquitetura Geral

**Tags:** #confiapix #arquitetura #clean-architecture

[[README|← Voltar ao índice]]

---

## Princípios

O backend segue **arquitetura modular baseada em domínio**, inspirada em DDD e Clean Architecture:

- Cada módulo representa um **bounded context**
- Camadas internas por módulo: `controller → service → repository → entity`
- DTOs separados das entidades JPA
- MapStruct para conversão entre camadas
- Regras de negócio concentradas nos **services**

---

## Diagrama de camadas

> **Diagrama Canvas:** [[canvas/arquitetura-camadas.canvas|Arquitetura em Camadas]]

---

## Módulos do pacote `com.confiapix`

| Módulo | Responsabilidade |
|--------|------------------|
| `auth` | Registro, login, entidade User |
| `tenant` | Entidade Tenant, TenantContextHolder |
| `company` | CRUD de empresas |
| `customer` | CRUD de clientes |
| `supplier` | CRUD de fornecedores |
| `receivable` | Contas a receber |
| `payable` | Contas a pagar |
| `dashboard` | Agregações financeiras |
| `common` | ApiResponse, exceptions, enums, BaseEntity |
| `security` | JwtService, JwtAuthenticationFilter |
| `config` | SecurityConfig, OpenApiConfig |

Detalhes em [[02-arquitetura/estrutura-modular|Estrutura Modular]].

---

## Padrão de resposta da API

Todas as respostas seguem o envelope `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Mensagem opcional",
  "data": { },
  "timestamp": "2026-06-04T12:00:00Z"
}
```

Listagens paginadas encapsulam `PageResponse<T>` dentro de `data`:

```json
{
  "success": true,
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

---

## Tratamento de erros

Centralizado em `GlobalExceptionHandler`:

| Exceção | HTTP | Descrição |
|---------|------|-----------|
| `ResourceNotFoundException` | 404 | Recurso não encontrado |
| `BusinessException` | 400 | Violação de regra de negócio |
| `BadCredentialsException` | 401 | Login inválido |
| `AccessDeniedException` | 403 | Sem permissão |
| `MethodArgumentNotValidException` | 400 | Erro de validação Bean Validation |
| `Exception` (genérica) | 500 | Erro interno |

---

## Multi-tenant na arquitetura

> **Diagrama Canvas:** [[canvas/multi-tenant-isolamento.canvas|Isolamento Multi-Tenant]]

Ver [[04-regras-negocio/multi-tenant|Regras Multi-Tenant]] e [[08-seguranca/jwt-e-autenticacao|JWT]].

---

## Documentos relacionados

- [[02-arquitetura/estrutura-modular|Estrutura Modular]]
- [[02-arquitetura/stack-tecnologica|Stack Tecnológica]]
- [[05-fluxos/requisicao-autenticada|Fluxo de Requisição Autenticada]]

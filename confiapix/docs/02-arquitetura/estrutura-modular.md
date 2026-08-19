# Estrutura Modular

**Tags:** #confiapix #arquitetura #modulos

[[02-arquitetura/arquitetura-geral|← Arquitetura Geral]]

---

## Árvore de diretórios

```
src/main/java/com/ConfiaPix/
├── ConfiapixApplication.java
├── auth/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── tenant/
│   ├── entity/
│   ├── repository/
│   └── context/
├── company/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── mapper/
├── customer/       (mesma estrutura)
├── supplier/       (mesma estrutura)
├── receivable/     (mesma estrutura)
├── payable/        (mesma estrutura)
├── dashboard/
│   ├── controller/
│   ├── service/
│   └── dto/
├── common/
│   ├── dto/
│   ├── entity/
│   ├── enums/
│   ├── exception/
│   └── util/
├── security/
└── config/
```

---

## Convenções por camada

### Controller

- Anotação `@RestController` + `@RequestMapping`
- Recebe DTOs de entrada com `@Valid`
- Retorna `ApiResponse<T>`
- Documentado com `@Operation` (OpenAPI)
- Endpoints protegidos usam `@SecurityRequirement(name = "bearerAuth")`

### Service

- Contém **regras de negócio**
- Usa `@Transactional` (readOnly para consultas)
- Obtém tenant via `TenantContextHolder.getTenantId()`
- Lança `BusinessException` ou `ResourceNotFoundException`

### Repository

- Interface JPA estendendo `JpaRepository`
- Queries customizadas com `@Query` para filtro de tenant
- Sem lógica de negócio

### Entity

- Estende `BaseEntity` (id UUID, createdAt, updatedAt)
- JPA Auditing habilitado via `@EnableJpaAuditing`
- Relacionamentos `LAZY` por padrão

### DTO

- `*Request` — entrada (com Bean Validation)
- `*Response` — saída (sem dados sensíveis)

### Mapper

- Interface MapStruct (`componentModel = "spring"`)
- Ignora campos gerenciados pelo sistema (id, tenant, timestamps)

---

## Dependências entre módulos

> **Diagrama Canvas:** [[canvas/dependencias-modulos.canvas|Dependências entre Módulos]]

**Regra:** módulos de domínio **nunca** dependem de `controller`. Services podem usar services de outros módulos para validação de pertencimento (ex.: `ReceivableService` usa `CompanyService` e `CustomerService`).

---

## Recursos

```
src/main/resources/
├── application.yml
└── db/migration/
    └── V1__initial_schema.sql
```

# Stack Tecnológica

**Tags:** #ConfiaPix #stack #tecnologia

[[02-arquitetura/arquitetura-geral|← Arquitetura Geral]]

---

## Dependências principais

| Biblioteca | Versão | Uso |
|------------|--------|-----|
| Spring Boot | 3.4.2 | Framework base |
| Spring Web | — | REST API |
| Spring Data JPA | — | Persistência |
| Spring Security | — | Autenticação/autorização |
| Spring Validation | — | Bean Validation |
| PostgreSQL Driver | — | Banco de dados |
| Flyway | — | Migrations |
| Lombok | — | Redução de boilerplate |
| MapStruct | 1.6.3 | Mapeamento DTO ↔ Entity |
| JJWT | 0.12.6 | Geração/validação JWT |
| SpringDoc OpenAPI | 2.8.4 | Swagger UI |

---

## Requisitos de runtime

| Requisito | Versão |
|-----------|--------|
| JDK | 21 |
| PostgreSQL | 16+ (recomendado) |
| Maven | 3.9+ (ou `mvnw.cmd`) |

---

## Configuração JPA

| Propriedade | Valor | Motivo |
|-------------|-------|--------|
| `ddl-auto` | `validate` | Schema controlado pelo Flyway |
| `open-in-view` | `false` | Evita lazy loading fora de transação |
| `time_zone` | UTC | Consistência de timestamps |

---

## Build

```bash
# Compilar e testar
mvnw.cmd clean package

# Executar
mvnw.cmd spring-boot:run
```

---

## Documentos relacionados

- [[09-operacao/configuracao-e-deploy|Configuração e Deploy]]
- [[09-operacao/variaveis-ambiente|Variáveis de Ambiente]]

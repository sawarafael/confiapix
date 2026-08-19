# Flyway Migrations

**Tags:** #ConfiaPix #banco-dados #flyway

[[07-banco-dados/schema|← Schema PostgreSQL]]

---

## Configuração

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
  jpa:
    hibernate:
      ddl-auto: validate
```

> O Hibernate **valida** o schema mas **não cria** tabelas. Toda alteração estrutural deve passar por migration Flyway.

---

## Histórico de migrations

| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| V1 | `V1__initial_schema.sql` | Schema inicial completo do MVP |

---

## Convenção de nomenclatura

```
V{versao}__{descricao_snake_case}.sql
```

Exemplos futuros:

```
V2__add_user_roles.sql
V3__add_cancel_reason_to_accounts.sql
```

---

## Fluxo de deploy

> **Diagrama Canvas:** [[canvas/fluxo-flyway.canvas|Fluxo Flyway]]

---

## Boas práticas

1. **Nunca** altere migrations já aplicadas em produção
2. Sempre crie nova versão para mudanças
3. Teste migrations em ambiente local antes do deploy
4. Mantenha migrations idempotentes quando possível (Flyway garante execução única)

---

## Comandos úteis

```bash
# Ver status das migrations (requer Flyway CLI ou logs na subida)
mvnw.cmd spring-boot:run
# Logs: "Successfully applied X migration(s)"
```

# Variáveis de Ambiente

**Tags:** #confiapix #operacao #config

[[09-operacao/configuracao-e-deploy|← Configuração e Deploy]]

---

## Referência completa

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/confiapix` | JDBC URL do PostgreSQL |
| `DATABASE_URL` | — | URL `postgres://` do Render; o entrypoint converte para JDBC se `DB_URL` estiver vazio |
| `DB_USERNAME` | `confiapix` | Usuário do banco |
| `DB_PASSWORD` | `confiapix` | Senha do banco |
| `PORT` / `SERVER_PORT` | `8080` | Porta HTTP (o Render injeta `PORT`) |
| `JWT_SECRET` | *(dev only)* | Chave secreta HMAC para JWT (≥ 32 caracteres) |
| `JWT_EXPIRATION_MS` | `86400000` | Expiração do token (ms) |
| `ENCRYPTION_SECRET` | *(dev only)* | Chave de criptografia das credenciais bancárias |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200,...` | Origens do painel (desnecessário se o nginx fizer proxy) |
| `API_UPSTREAM` | `http://app:8080` | Host interno da API (container do frontend) |

---

## Exemplo — desenvolvimento

```bash
set DB_URL=jdbc:postgresql://localhost:5432/confiapix
set DB_USERNAME=ConfiaPix
set DB_PASSWORD=ConfiaPix
set JWT_SECRET=minha-chave-secreta-dev-com-pelo-menos-32-caracteres
mvnw.cmd spring-boot:run
```

---

## Exemplo — docker-compose

Definidas em `docker-compose.yml`:

```yaml
environment:
  DB_URL: jdbc:postgresql://postgres:5432/confiapix
  DB_USERNAME: confiapix
  DB_PASSWORD: confiapix
  JWT_SECRET: confiapix-docker-secret-key-change-in-production-min-256-bits-long-enough
```

---

## Produção

> ⚠️ **Nunca** use valores padrão de `JWT_SECRET` ou credenciais de banco em produção.

Checklist:
- [ ] `JWT_SECRET` com ≥ 32 caracteres aleatórios
- [ ] Credenciais de banco via secrets manager
- [ ] HTTPS terminado no load balancer
- [ ] Logs sem exposição de tokens

---

## application.yml

Arquivo base: `src/main/resources/application.yml`

Perfil de teste: `src/test/resources/application-test.yml` (H2 in-memory, Flyway desabilitado).

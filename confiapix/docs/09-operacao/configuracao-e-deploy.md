# Configuração e Deploy

**Tags:** #ConfiaPix #operacao #deploy

[[README|← Voltar ao índice]]

---

## Pré-requisitos

- JDK 21 (`JAVA_HOME` configurado)
- PostgreSQL 16+ (local ou Docker)
- Maven 3.9+ ou `mvnw.cmd`

---

## Opção 1 — Docker Compose (recomendado)

```bash
docker-compose up --build
```

Sobe:
- **postgres** na porta `5432`
- **app** na porta `8080`

Stack completa (API + Postgres + painel nginx, como no Render):

```bash
docker compose --profile full up --build
```

Painel em http://localhost:8081.

---

## Opção 2 — Execução local

### 1. Subir PostgreSQL

```bash
docker-compose up postgres -d
```

Ou use instância local com credenciais:

| Variável | Padrão |
|----------|--------|
| DB | ConfiaPix |
| User | ConfiaPix |
| Password | ConfiaPix |

### 2. Executar aplicação

```bash
mvnw.cmd spring-boot:run
```

---

## Verificação

| URL | Descrição |
|-----|-----------|
| http://localhost:8080/swagger-ui.html | Swagger UI |
| http://localhost:8080/api-docs | OpenAPI JSON |

Teste rápido:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"tenantName\":\"Teste\",\"name\":\"Admin\",\"email\":\"test@test.com\",\"password\":\"123456\"}"
```

---

## Dockerfile

Multi-stage build:
1. **build** — Maven 3.9 + JDK 21 compilam o JAR
2. **runtime** — JRE 21 Alpine, usuário não-root `confiapix`
3. **entrypoint** — converte `DATABASE_URL` (Render) em JDBC e escuta `PORT`

Painel Angular: `confiapix-frontend/Dockerfile` (nginx + proxy para a API).

---

## Opção 3 — Render (Docker)

O Render **não** sobe `docker-compose`. Cada serviço usa o próprio `Dockerfile`. O arquivo `render.yaml` na raiz do monorepo define:

| Serviço | Tipo | Imagem |
|---------|------|--------|
| `confiapix-db` | PostgreSQL 16 | gerenciado |
| `confiapix-api` | Web Service | `confiapix/Dockerfile` |
| `confiapix-frontend` | Web Service | `confiapix-frontend/Dockerfile` (nginx) |
| `confiapix-website` | Static Site | pasta estática |

No dashboard: **New → Blueprint** e aponte para o repositório. O `render.yaml` usa **plano free** (não pede cartão).

Limitações do free:
- API e painel **hibernam** após 15 min sem tráfego (próximo acesso demora ~1 min).
- Postgres free **expira em 30 dias** e só pode existir **1** por workspace.
- 512 MB de RAM: se a API cair com OOM, aí sim precisaria de plano pago.
- Webhook Stone não é confiável no free (serviço dorme).

Checklist:
- Confirme que `JWT_SECRET` tem ≥ 32 caracteres.
- Não rotacione `ENCRYPTION_SECRET` depois de gravar credenciais Stone.
- Se o painel não falar com a API, copie a URL pública do `confiapix-api` para a env `API_UPSTREAM` do frontend.
- Webhook Stone: `https://confiapix-api.onrender.com/api/v1/webhooks/stone/pix`

Teste local no mesmo formato:

```bash
cd confiapix
docker compose --profile full up --build
```

Admin em http://localhost:8081 — API em http://localhost:8080.

---

## Documentos relacionados

- [[09-operacao/variaveis-ambiente|Variáveis de Ambiente]]
- [[07-banco-dados/migrations|Flyway Migrations]]

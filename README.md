# FLUXPAY — ConfiaPix

Monorepo com backend e painel administrativo.

| Pasta | Nome convencional | Descrição | Porta padrão |
|-------|-------------------|-----------|--------------|
| `confiapix/` | **confiapix-api** | API REST Spring Boot (JWT, Stone, PIX) | 8080 |
| `confiapix-frontend/` | **confiapix-frontend** | Painel admin Angular 22 | 4200 |
| `confiapix-website/` | **confiapix-website** | Site institucional / portfólio de vendas | 4300 |

## Subir tudo

```powershell
# API + Postgres
cd confiapix
docker compose up --build -d

# Frontend (outro terminal)
cd confiapix-frontend
npm start
```

Stack completa em Docker (como no Render):

```powershell
cd confiapix
docker compose --profile full up --build
```

- API: http://localhost:8080
- Admin (dev): http://localhost:4200
- Admin (Docker/nginx): http://localhost:8081
- Site institucional: http://localhost:4300
- Swagger: http://localhost:8080/swagger-ui.html

Deploy no Render: use o `render.yaml` na raiz (API e painel em Docker + Postgres gerenciado). Detalhes em `confiapix/docs/09-operacao/configuracao-e-deploy.md`.

```powershell
# Site de portfólio / pré-venda (outro terminal)
cd confiapix-website
npx --yes serve -l 4300
```

## Variáveis úteis

| Variável | Onde | Default |
|----------|------|---------|
| `CORS_ALLOWED_ORIGINS` | confiapix-api | `http://localhost:4200` |

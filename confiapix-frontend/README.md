# confiapix-frontend

Painel administrativo **ConfiaPix** — Angular 22 (standalone, signals).

## Pré-requisitos

- Node.js 20+
- **confiapix-api** rodando em http://localhost:8080

## Desenvolvimento

```powershell
cd confiapix-frontend
npm install
npm start
```

Abra http://localhost:4200

## Primeiro acesso

1. Faça **login** com credenciais existentes (sem registro público no painel)
2. **Admin operador da plataforma** → menu **Acessos** para provisionar empresas clientes
3. **Admin de empresa cliente** → Dashboard, PIX, Stone, etc.

> O primeiro tenant cadastrado na API vira operador da plataforma (migration V5).

## Estrutura

```
src/app/
  core/          auth, models, services HTTP
  layout/        sidebar + header (glassmorphism)
  features/      dashboard, pix, conciliações, stone, settings
  shared/        stat-card, charts SVG
```

## Build produção

```powershell
npm run build
```

Configure `src/environments/environment.prod.ts` com a URL da API.

## Convenção de nomes

| Projeto | Pasta |
|---------|-------|
| confiapix-api | `../confiapix` |
| confiapix-frontend | esta pasta |

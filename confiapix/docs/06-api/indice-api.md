# Referência da API

**Tags:** #ConfiaPix #api #rest

[[README|← Voltar ao índice]]

---

## Base URL

```
http://localhost:8080
```

## Autenticação

Endpoints protegidos exigem:

```
Authorization: Bearer {jwt_token}
```

## Formato de resposta

Todas as respostas usam envelope `ApiResponse<T>`.

## Paginação

Parâmetros de query em endpoints de listagem:

| Parâmetro | Padrão | Descrição |
|-----------|--------|-----------|
| `page` | 0 | Página (zero-based) |
| `size` | 20 | Itens por página |
| `sort` | varia | Campo de ordenação |

Exemplo: `GET /companies?page=0&size=10&sort=createdAt,desc`

---

## Índice de endpoints

| Módulo | Documento |
|--------|-----------|
| Auth | [[06-api/auth|Auth]] |
| Companies | [[06-api/companies|Companies]] |
| Customers | [[06-api/customers|Customers]] |
| Suppliers | [[06-api/suppliers|Suppliers]] |
| Receivables | [[06-api/receivables|Receivables]] |
| Payables | [[06-api/payables|Payables]] |
| Dashboard | [[06-api/dashboard|Dashboard]] |

---

## Resumo de endpoints

| Método | Path | Auth | Descrição |
|--------|------|------|-----------|
| POST | `/auth/register` | Não | Registrar tenant + user |
| POST | `/auth/login` | Não | Login |
| GET | `/companies` | Sim | Listar empresas |
| GET | `/companies/{id}` | Sim | Buscar empresa |
| POST | `/companies` | Sim | Criar empresa |
| PUT | `/companies/{id}` | Sim | Atualizar empresa |
| DELETE | `/companies/{id}` | Sim | Excluir empresa |
| GET | `/customers` | Sim | Listar clientes |
| GET | `/customers/{id}` | Sim | Buscar cliente |
| POST | `/customers` | Sim | Criar cliente |
| PUT | `/customers/{id}` | Sim | Atualizar cliente |
| DELETE | `/customers/{id}` | Sim | Excluir cliente |
| GET | `/suppliers` | Sim | Listar fornecedores |
| GET | `/suppliers/{id}` | Sim | Buscar fornecedor |
| POST | `/suppliers` | Sim | Criar fornecedor |
| PUT | `/suppliers/{id}` | Sim | Atualizar fornecedor |
| DELETE | `/suppliers/{id}` | Sim | Excluir fornecedor |
| GET | `/receivables` | Sim | Listar contas a receber |
| GET | `/receivables/{id}` | Sim | Buscar conta a receber |
| POST | `/receivables` | Sim | Criar conta a receber |
| PUT | `/receivables/{id}` | Sim | Atualizar conta a receber |
| PATCH | `/receivables/{id}/pay` | Sim | Baixar conta a receber |
| DELETE | `/receivables/{id}` | Sim | Excluir conta a receber |
| GET | `/payables` | Sim | Listar contas a pagar |
| GET | `/payables/{id}` | Sim | Buscar conta a pagar |
| POST | `/payables` | Sim | Criar conta a pagar |
| PUT | `/payables/{id}` | Sim | Atualizar conta a pagar |
| PATCH | `/payables/{id}/pay` | Sim | Baixar conta a pagar |
| DELETE | `/payables/{id}` | Sim | Excluir conta a pagar |
| GET | `/dashboard` | Sim | Indicadores financeiros |

---

## Swagger interativo

- UI: http://localhost:8080/swagger-ui.html
- JSON: http://localhost:8080/api-docs

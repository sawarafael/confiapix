# ConfiaPix

Este vault contém a documentação técnica do backend ConfiaPix MVP 1.0.

## Como usar no Obsidian

1. **Abrir como vault:** File → Open folder → selecione a pasta `docs/`
2. **Página inicial:** abra [[README]]
3. **Diagramas:** abra [[canvas/indice-diagramas.canvas|Índice de Diagramas Canvas]]
4. **Graph view:** Ctrl+G para visualizar conexões entre documentos

## Diagramas Canvas

Todos os fluxos, arquitetura e regras visuais estão em arquivos `.canvas` na pasta `canvas/`:

| Canvas | Conteúdo |
|--------|----------|
| [[canvas/indice-diagramas.canvas|Índice]] | Hub central — links para todos os diagramas |
| [[canvas/mapa-sistema.canvas|Mapa do Sistema]] | Módulos e relacionamentos |
| [[canvas/modelo-dominio-er.canvas|Modelo ER]] | Entidades e tabelas |
| [[canvas/arquitetura-camadas.canvas|Arquitetura]] | Camadas Clean Architecture |
| [[canvas/multi-tenant-isolamento.canvas|Multi-Tenant]] | Isolamento Shared Database |
| [[canvas/fluxo-autenticacao.canvas|Autenticação]] | Registro e login |
| [[canvas/status-contas-estados.canvas|Status de Contas]] | Máquina de estados |

Lista completa em [[canvas/indice-diagramas.canvas]].

## Estrutura

```
docs/
├── README.md
├── canvas/                   ← Diagramas Obsidian (.canvas)
├── 01-visao-geral/
├── 02-arquitetura/
├── 03-dominio/
├── 04-regras-negocio/
├── 05-fluxos/
├── 06-api/
├── 07-banco-dados/
├── 08-seguranca/
└── 09-operacao/
```

## Tags principais

- `#ConfiaPix` — todos os documentos
- `#regra-negocio` — regras de negócio
- `#fluxo` — fluxos de processo
- `#api` — referência REST

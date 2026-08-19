# ConfiaPix — site institucional

Landing de portfólio e pré-venda do ConfiaPix. Apresenta o produto, os recursos do painel, as integrações bancárias e captura pedidos de demonstração.

## Ver localmente

Abra o arquivo `index.html` no navegador ou sirva a pasta:

```powershell
cd confiapix-website
npx --yes serve -l 4300
```

Depois acesse http://localhost:4300

## Ajustar contato comercial

Em `js/main.js`:

```js
const SITE = {
  email: "contato@confiapix.com.br",
  whatsapp: "", // ex.: 5511999990000
};
```

Se `whatsapp` estiver preenchido, o link comercial aparece na seção de contato.

## Publicar

A pasta é estática (HTML, CSS e JS). Pode ir para Vercel, Netlify, GitHub Pages ou qualquer hospedagem de site estático apontando a raiz para `confiapix-website/`.

# 📮 Guia Postman - Busca de Produtos por Imagem

## 🚀 Configuração da Requisição no Postman

### Passo 1: Criar Nova Requisição

1. Abra o Postman
2. Clique em **"New"** → **"HTTP Request"**
3. Ou use o atalho: `Ctrl + N` (Windows/Linux) ou `Cmd + N` (Mac)

### Passo 2: Configurar Método e URL

1. **Método HTTP**: Selecione **`POST`** no dropdown
2. **URL**: Digite a URL completa:

   ```
   http://localhost:8080/products/search-by-image
   ```

   **Ou se estiver usando Docker:**

   ```
   http://localhost:8080/products/search-by-image
   ```

   **Ou se estiver em produção:**

   ```
   https://seu-dominio.com/products/search-by-image
   ```

### Passo 3: Configurar Headers

1. Vá para a aba **"Headers"**
2. **NÃO** adicione o header `Content-Type` manualmente
3. O Postman adicionará automaticamente `multipart/form-data` quando você selecionar o body correto

### Passo 4: Configurar Body (IMPORTANTE)

1. Vá para a aba **"Body"**
2. Selecione **"form-data"** (NÃO use "raw" ou "binary")
3. Na primeira linha, configure:
   - **Key**: Digite `image`
   - **Tipo**: Clique no dropdown à direita do campo Key e selecione **"File"** (não "Text")
   - **Value**: Clique em **"Select Files"** e escolha uma imagem do seu computador

### Passo 5: Selecionar Imagem

1. Clique em **"Select Files"**
2. Escolha uma imagem (formato: JPG, PNG, GIF, BMP)
3. Tamanho máximo: 10MB
4. **Dica**: Use uma imagem de móvel para melhores resultados

### Passo 6: Enviar Requisição

1. Clique no botão **"Send"** (azul, no canto superior direito)
2. Aguarde a resposta

## 📋 Resumo Visual da Configuração

```
┌─────────────────────────────────────────────────────────┐
│ POST  http://localhost:8080/products/search-by-image   │
├─────────────────────────────────────────────────────────┤
│ Headers: (vazio - Postman adiciona automaticamente)     │
├─────────────────────────────────────────────────────────┤
│ Body: form-data                                          │
│                                                          │
│  Key: image  [File ▼]  [Select Files...]               │
│  └─ Tipo: File (não Text!)                             │
│  └─ Value: [arquivo selecionado]                       │
└─────────────────────────────────────────────────────────┘
```

## ✅ Exemplo de Resposta de Sucesso (200 OK)

```json
{
  "status": "200",
  "message": "Busca realizada com sucesso.",
  "data": [
    {
      "detectedLabels": ["furniture", "chair", "wood", "seat"],
      "detectedColors": ["#8B4513", "#D2B48C", "#DEB887"],
      "detectedObjects": ["chair", "furniture"],
      "matchedProducts": [
        {
          "productId": 2,
          "productName": "Cadeira de Cozinha Moderna",
          "similarityScore": 0.85,
          "matchReasons": ["tipo: chair", "cor: #8B4513", "objeto: furniture"]
        },
        {
          "productId": 5,
          "productName": "Poltrona Reclinável Premium",
          "similarityScore": 0.65,
          "matchReasons": ["tipo: chair", "objeto: furniture"]
        }
      ],
      "searchMethod": "AI_VISION"
    }
  ]
}
```

## ⚠️ Resposta com Fallback (API não disponível)

Se a Google Vision API não estiver disponível, você verá:

```json
{
  "status": "200",
  "message": "Busca realizada com sucesso.",
  "data": [
    {
      "detectedLabels": [],
      "detectedColors": ["#8B4513"],
      "detectedObjects": [],
      "matchedProducts": [
        {
          "productId": 2,
          "productName": "Cadeira de Cozinha Moderna",
          "similarityScore": 0.5,
          "matchReasons": ["cor: #8B4513"]
        }
      ],
      "searchMethod": "BASIC_FALLBACK"
    }
  ]
}
```

## ❌ Possíveis Erros

### Erro 400: "Imagem não fornecida ou vazia"

**Causa**: Não selecionou arquivo ou selecionou tipo errado no Postman

**Solução**:

1. Certifique-se de que selecionou **"File"** (não "Text") no tipo do campo `image`
2. Selecione um arquivo de imagem válido

### Erro 500: "Erro ao processar busca por imagem"

**Causa**: Imagem corrompida ou formato não suportado

**Solução**:

1. Use formatos: JPG, PNG, GIF, BMP
2. Verifique se a imagem não está corrompida
3. Tente com outra imagem

### Erro de Conexão

**Causa**: Backend não está rodando

**Solução**:

1. Verifique se o backend está rodando:

   ```bash
   # Se estiver usando Docker
   docker ps

   # Se estiver rodando localmente
   # Verifique os logs da aplicação
   ```

2. Verifique se a porta 8080 está correta
3. Verifique se não há firewall bloqueando

## 🎯 Dicas para Melhores Resultados

1. **Use imagens claras**: Imagens bem iluminadas funcionam melhor
2. **Objeto centralizado**: O móvel deve estar no centro da imagem
3. **Fundo simples**: Fundos simples facilitam a detecção
4. **Tamanho adequado**: Imagens muito pequenas podem não funcionar bem
5. **Formato**: JPG e PNG funcionam melhor que GIF

## 📸 Exemplo de Imagem Ideal

- ✅ Móvel visível e centralizado
- ✅ Boa iluminação
- ✅ Fundo simples
- ✅ Resolução adequada (não muito pequena)

## 🔄 Testando com cURL (Alternativa)

Se preferir testar via terminal:

```bash
curl -X POST http://localhost:8080/products/search-by-image \
  -F "image=@/caminho/para/sua/imagem.jpg"
```

## 📝 Checklist Rápido

Antes de enviar a requisição, verifique:

- [ ] Método: **POST**
- [ ] URL: `http://localhost:8080/products/search-by-image`
- [ ] Body: **form-data** (não raw!)
- [ ] Key: `image`
- [ ] Tipo: **File** (não Text!)
- [ ] Arquivo selecionado
- [ ] Backend rodando

## 🐛 Troubleshooting no Postman

### Problema: "Key deve ser 'image'"

**Solução**: Certifique-se de que o campo Key está exatamente como `image` (minúsculo, sem espaços)

### Problema: "Content-Type não é multipart/form-data"

**Solução**:

1. Use **form-data** no Body (não raw)
2. Não adicione o header Content-Type manualmente
3. Deixe o Postman adicionar automaticamente

### Problema: Arquivo não é enviado

**Solução**:

1. Certifique-se de que selecionou **"File"** no tipo do campo
2. Se estiver como "Text", mude para "File"
3. Selecione o arquivo novamente

## 📚 Estrutura da Resposta

- **status**: Código HTTP (200 = sucesso)
- **message**: Mensagem descritiva
- **data**: Array com um objeto contendo:
  - **detectedLabels**: Lista de etiquetas detectadas pela IA
  - **detectedColors**: Lista de cores dominantes (hexadecimal)
  - **detectedObjects**: Lista de objetos detectados
  - **matchedProducts**: Lista de produtos similares encontrados
    - **productId**: ID do produto
    - **productName**: Nome do produto
    - **similarityScore**: Score de similaridade (0.0 a 1.0)
    - **matchReasons**: Razões da correspondência
  - **searchMethod**: Método usado ("AI_VISION" ou "BASIC_FALLBACK")


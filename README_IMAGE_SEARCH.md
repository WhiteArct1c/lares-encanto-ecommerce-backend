# 🔍 Busca de Produtos por Imagem - Google Cloud Vision API

## 📋 Funcionalidade

Sistema de busca de produtos usando análise de imagem com Google Cloud Vision API. O sistema analisa uma imagem de móvel, extrai características (cor, tipo, objetos), e recomenda produtos similares do catálogo.

## 🚀 Endpoint

```
POST /products/search-by-image
Content-Type: multipart/form-data

Body:
  image: [arquivo de imagem]
```

### Resposta de Sucesso (200 OK)

```json
{
  "status": "200",
  "message": "Busca realizada com sucesso.",
  "data": [
    {
      "detectedLabels": ["furniture", "chair", "wood"],
      "detectedColors": ["#8B4513", "#D2B48C"],
      "detectedObjects": ["chair", "furniture"],
      "matchedProducts": [
        {
          "productId": 1,
          "productName": "Cadeira de Cozinha Moderna",
          "similarityScore": 0.85,
          "matchReasons": ["tipo: chair", "cor: #8B4513", "objeto: furniture"]
        }
      ],
      "searchMethod": "AI_VISION"
    }
  ]
}
```

## ⚙️ Configuração

### 1. Variável de Ambiente (Recomendado)

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/caminho/para/google-credentials.json"
```

### 2. Arquivo no Projeto (Alternativa)

O arquivo `src/main/resources/google-credentials.json` já está configurado com as credenciais fornecidas.

⚠️ **IMPORTANTE**: Este arquivo está no `.gitignore` para não ser commitado.

## 🎯 Como Funciona

1. **Análise de Imagem**: Google Vision API extrai:

   - Labels (etiquetas): "furniture", "chair", "sofa", etc.
   - Cores dominantes: valores hexadecimais (#RRGGBB)
   - Objetos detectados: objetos identificados na imagem

2. **Busca Inteligente**: Compara características extraídas com produtos do catálogo:

   - Tipo de móvel (type)
   - Cor do produto
   - Nome do produto
   - Descrição

3. **Score de Similaridade**: Calcula pontuação baseada em:

   - Tipo: 30%
   - Cor: 25%
   - Objeto: 25%
   - Nome: 20%
   - Descrição: 10%

4. **Fallback**: Se a API não estiver disponível ou limite atingido, usa análise básica de cor dominante.

## 💰 Otimizações para Tier Gratuito

### Limites do Free Tier

- **1.000 requisições/mês** gratuitas
- Após o limite, usa fallback automático

### Implementações de Otimização

1. **Cache de Resultados**

   - Resultados de análise são cacheados por 24 horas
   - Evita requisições duplicadas para a mesma imagem

2. **Rate Limiting**

   - Contador automático de requisições
   - Fallback automático quando limite é atingido

3. **Análise Eficiente**

   - Máximo de 10 labels por imagem
   - Máximo de 10 objetos detectados
   - Filtro de score mínimo (0.5 para labels, 0.1 para cores)

4. **Fallback Inteligente**
   - Análise básica de cor dominante quando API não disponível
   - Busca por cor similar no catálogo

## 📊 Métricas e Monitoramento

O sistema registra logs com:

- Método de busca usado (AI_VISION ou BASIC_FALLBACK)
- Número de matches encontrados
- Labels, cores e objetos detectados

## 🔒 Segurança

- Endpoint público (permitAll) para facilitar uso
- Validação de tipo de arquivo (imagem)
- Limite de tamanho: 10MB (configurado em `application.properties`)

## 🧪 Testando

### Usando cURL

```bash
curl -X POST http://localhost:8080/products/search-by-image \
  -F "image=@/caminho/para/imagem.jpg"
```

### Usando Postman

1. Método: POST
2. URL: `http://localhost:8080/products/search-by-image`
3. Body: form-data
4. Key: `image` (tipo: File)
5. Value: selecione uma imagem

## 📝 Notas Técnicas

- **Biblioteca**: Google Cloud Vision API v3.11.0
- **Cache**: Caffeine (Spring Cache)
- **Formato de Imagem**: JPEG, PNG, GIF, BMP
- **Tamanho Máximo**: 10MB
- **Timeout**: 15 segundos por requisição

## 🐛 Troubleshooting

### Erro: "Vision API client not available"

- Verifique se `GOOGLE_APPLICATION_CREDENTIALS` está configurado
- Verifique se o arquivo JSON de credenciais existe e está válido
- Verifique se a Cloud Vision API está habilitada no Google Cloud Console

### Erro: "Free tier limit reached"

- O sistema automaticamente usa fallback
- Aguarde o próximo mês ou atualize para plano pago

### Resultados vazios

- Tente com imagem mais clara e com objeto centralizado
- Verifique se há produtos similares no catálogo

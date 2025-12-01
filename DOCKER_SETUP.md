# 🐳 Configuração Docker - Google Cloud Vision API

## 📋 Configuração Atual

O Dockerfile e docker-compose.yml foram atualizados para incluir suporte ao Google Cloud Vision API.

## ✅ O que foi configurado

### 1. **Dockerfile**
- Copia o arquivo `google-credentials.json` para `/app/google-credentials.json` no container
- Define a variável de ambiente `GOOGLE_APPLICATION_CREDENTIALS=/app/google-credentials.json`

### 2. **docker-compose.yml**
- Adiciona a variável de ambiente `GOOGLE_APPLICATION_CREDENTIALS` no serviço backend

### 3. **.dockerignore**
- Criado para otimizar o build (exclui arquivos desnecessários)

## 🚀 Como usar

### ⚠️ IMPORTANTE: Arquivo de Credenciais

O Dockerfile espera que o arquivo `src/main/resources/google-credentials.json` exista. Se não existir, o build falhará.

**Verifique se o arquivo existe:**
```bash
ls -la src/main/resources/google-credentials.json
```

Se não existir, você precisa criá-lo com as credenciais do Google Cloud.

### Opção 1: Usar arquivo de credenciais no projeto (Recomendado)

O arquivo `src/main/resources/google-credentials.json` já está configurado no projeto e será copiado automaticamente para o container durante o build.

**Build e execução:**
```bash
docker-compose build backend
docker-compose --profile backend up
```

### Opção 2: Usar volume mount (Alternativa)

Se preferir usar um arquivo de credenciais externo (não commitado no projeto):

1. **Comente a linha COPY no Dockerfile** (linha 16):
```dockerfile
# COPY src/main/resources/google-credentials.json /app/google-credentials.json
```

2. **Atualize o docker-compose.yml** para usar volume:
```yaml
backend:
  # ... outras configurações ...
  volumes:
    - /caminho/para/seu/google-credentials.json:/app/google-credentials.json:ro
  environment:
    # ... outras variáveis ...
    GOOGLE_APPLICATION_CREDENTIALS: /app/google-credentials.json
```

3. **Build e execução:**
```bash
docker-compose build backend
docker-compose --profile backend up
```

### Opção 3: Passar credenciais via variável de ambiente (Avançado)

Se preferir passar o JSON diretamente como variável de ambiente:

1. **Atualize o docker-compose.yml:**
```yaml
backend:
  # ... outras configurações ...
  environment:
    # ... outras variáveis ...
    GOOGLE_APPLICATION_CREDENTIALS_JSON: ${GOOGLE_APPLICATION_CREDENTIALS_JSON}
```

2. **Crie um script de inicialização** que escreve o JSON em arquivo:
```bash
#!/bin/sh
if [ -n "$GOOGLE_APPLICATION_CREDENTIALS_JSON" ]; then
  echo "$GOOGLE_APPLICATION_CREDENTIALS_JSON" > /app/google-credentials.json
  export GOOGLE_APPLICATION_CREDENTIALS=/app/google-credentials.json
fi
java -jar app.jar
```

## 🔍 Verificação

Após iniciar o container, verifique os logs:

```bash
docker logs backend
```

Você deve ver uma das seguintes mensagens:

**✅ Sucesso:**
```
Google Cloud Vision API client initialized successfully. Credentials: /app/google-credentials.json
```

**⚠️ Fallback (sem credenciais):**
```
GOOGLE_APPLICATION_CREDENTIALS not set. Google Vision API will not be available. Using fallback mode.
```

## 🐛 Troubleshooting

### Erro: "File not found: /app/google-credentials.json"

**Solução:**
1. Verifique se o arquivo `src/main/resources/google-credentials.json` existe
2. Verifique se o arquivo não está no `.gitignore` (deve estar, mas precisa existir localmente)
3. Rebuild a imagem: `docker-compose build --no-cache backend`

### Erro: "Failed to initialize Google Cloud Vision API client"

**Possíveis causas:**
1. Arquivo JSON inválido ou corrompido
2. Credenciais expiradas ou sem permissões
3. API não habilitada no Google Cloud Console

**Solução:**
1. Verifique o arquivo JSON
2. Verifique as permissões da Service Account no Google Cloud Console
3. Certifique-se de que a Cloud Vision API está habilitada

### Sistema usando fallback mesmo com credenciais configuradas

**Verificação:**
```bash
# Entre no container
docker exec -it backend bash

# Verifique a variável de ambiente
echo $GOOGLE_APPLICATION_CREDENTIALS

# Verifique se o arquivo existe
ls -la /app/google-credentials.json

# Verifique o conteúdo (primeiras linhas)
head -5 /app/google-credentials.json
```

## 📝 Notas Importantes

1. **Segurança**: O arquivo `google-credentials.json` contém chaves privadas. Nunca commite no Git (já está no `.gitignore`).

2. **Fallback**: O sistema funciona mesmo sem credenciais, usando análise básica de cor. A funcionalidade de busca por imagem continuará funcionando, mas com menor precisão.

3. **Build**: O arquivo de credenciais é copiado durante o build. Se você atualizar as credenciais, precisa fazer rebuild da imagem.

4. **Produção**: Para produção, considere usar:
   - Secrets do Docker/Kubernetes
   - Variáveis de ambiente gerenciadas
   - Service accounts com permissões mínimas

## 🔄 Atualizando Credenciais

Se precisar atualizar as credenciais:

1. Substitua o arquivo `src/main/resources/google-credentials.json`
2. Rebuild a imagem:
   ```bash
   docker-compose build --no-cache backend
   ```
3. Reinicie o container:
   ```bash
   docker-compose --profile backend up -d
   ```


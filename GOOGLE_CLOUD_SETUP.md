# Configuração do Google Cloud Vision API

## Passo a Passo para Criar Service Account

### 1. Acesse o Google Cloud Console

- URL: https://console.cloud.google.com/
- Certifique-se de estar no projeto: `les-ecommerce-lares-encanto`

### 2. Habilite a Cloud Vision API

1. Vá em **APIs & Services** → **Library**
2. Busque por "Cloud Vision API"
3. Clique em **Enable**

### 3. Crie uma Service Account

1. Vá em **IAM & Admin** → **Service Accounts**
2. Clique em **Create Service Account**
3. Preencha:
   - **Service account name**: `vision-api-service` (ou outro nome)
   - **Service account ID**: será gerado automaticamente
   - Clique em **Create and Continue**
4. **Grant this service account access to project**:
   - Role: Selecione **Cloud Vision API User**
   - Clique em **Continue**
5. Clique em **Done**

### 4. Baixe a Chave JSON

1. Na lista de Service Accounts, clique na que você criou
2. Vá na aba **Keys**
3. Clique em **Add Key** → **Create new key**
4. Selecione **JSON**
5. Clique em **Create**
6. O arquivo JSON será baixado automaticamente

### 5. Estrutura do JSON Correto

O arquivo JSON de Service Account deve ter esta estrutura:

```json
{
  "type": "service_account",
  "project_id": "les-ecommerce-lares-encanto",
  "private_key_id": "...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "vision-api-service@les-ecommerce-lares-encanto.iam.gserviceaccount.com",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "..."
}
```

### 6. Configuração no Backend

Após baixar o JSON, você tem duas opções:

**Opção A: Variável de Ambiente (Recomendado)**

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/caminho/para/seu/arquivo.json"
```

**Opção B: Colocar no projeto (menos seguro)**

- Coloque o arquivo em: `src/main/resources/google-credentials.json`
- Adicione ao `.gitignore` para não commitar

### 7. Verificação

Após configurar, teste se a API está acessível executando a aplicação.

## Importante

- ⚠️ **NUNCA** commite o arquivo JSON de credenciais no Git
- ⚠️ Adicione `*.json` de credenciais ao `.gitignore`
- ⚠️ O arquivo JSON contém chaves privadas sensíveis

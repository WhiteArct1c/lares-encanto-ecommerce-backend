# ⚠️ IMPORTANTE: JSON Incorreto

O JSON que você forneceu é de **OAuth 2.0 Client**, não de **Service Account**.

## ❌ O que você tem (OAuth Client):
```json
{
  "web": {
    "client_id": "...",
    "client_secret": "..."
  }
}
```
**Isso NÃO funciona para Google Cloud Vision API!**

## ✅ O que você precisa (Service Account):
```json
{
  "type": "service_account",
  "project_id": "les-ecommerce-lares-encanto",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...",
  "client_email": "...@...iam.gserviceaccount.com"
}
```

## 📋 Passo a Passo Correto:

### 1. Acesse: https://console.cloud.google.com/iam-admin/serviceaccounts?project=les-ecommerce-lares-encanto

### 2. Clique em "CREATE SERVICE ACCOUNT"

### 3. Preencha:
   - **Name**: `vision-api-service`
   - Clique em "CREATE AND CONTINUE"

### 4. Conceda permissão:
   - **Role**: Selecione `Cloud Vision API User`
   - Clique em "CONTINUE" → "DONE"

### 5. Baixe a chave:
   - Clique na Service Account criada
   - Aba **KEYS** → **ADD KEY** → **Create new key**
   - Selecione **JSON**
   - Clique em **CREATE**
   - O arquivo será baixado automaticamente

### 6. O arquivo baixado terá um nome como:
   `les-ecommerce-lares-encanto-xxxxx.json`

### 7. Envie esse arquivo JSON (ou seu conteúdo) para eu configurar

## 🔒 Segurança:
- ⚠️ O arquivo JSON contém chaves privadas
- ⚠️ NÃO compartilhe publicamente
- ⚠️ Já adicionei ao `.gitignore` para não commitar

## 📝 Após obter o JSON correto:
Envie o conteúdo do arquivo JSON baixado e eu implemento a solução completa!


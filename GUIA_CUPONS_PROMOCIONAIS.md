# Guia de Cupons Promocionais

## 📋 Visão Geral

O sistema suporta dois tipos de cupons:

1. **Cupons de Troca (EXCHANGE)**: Criados automaticamente quando uma troca/devolução é confirmada
2. **Cupons Promocionais (PROMOTIONAL)**: Criados manualmente pelo admin para campanhas e promoções

---

## 🎯 Como Funcionam os Cupons Promocionais

### Características

- ✅ Criados manualmente pelo admin
- ✅ Vinculados a um cliente específico
- ✅ Podem ter data de expiração (opcional)
- ✅ Valor pode ser usado parcialmente ou totalmente
- ✅ Desativados automaticamente quando totalmente utilizados
- ✅ Apenas **um** cupom promocional pode ser usado por compra (mas pode combinar com múltiplos cupons de troca)

### Regras de Uso

1. **Limite por compra**: Apenas 1 cupom promocional por pedido
2. **Múltiplos cupons de troca**: Pode usar múltiplos cupons de troca + 1 promocional
3. **Valor disponível**: O cupom só pode ser usado se tiver valor disponível (`value - usedValue > 0`)
4. **Validade**: O cupom deve estar ativo e não expirado (se tiver data de expiração)
5. **Propriedade**: O cupom só pode ser usado pelo cliente ao qual foi atribuído

---

## 🔐 Endpoints para Admin

### 1. Criar Cupom Promocional

**Endpoint**: `POST /coupons/promotional`  
**Permissão**: `ADMIN`  
**Autenticação**: Requerida

#### Request Body

```json
{
  "code": "PROMO-2024-001",
  "value": 100.00,
  "customerId": 1,
  "expiresAt": "2024-12-31T23:59:59"
}
```

#### Campos

- `code` (obrigatório, string, max 50 caracteres): Código único do cupom
- `value` (obrigatório, BigDecimal, min 0.01): Valor do cupom em reais
- `customerId` (obrigatório, Long, positivo): ID do cliente que receberá o cupom
- `expiresAt` (opcional, LocalDateTime): Data de expiração. Se `null`, o cupom não expira

#### Validações

- ✅ Código deve ser único no sistema
- ✅ Valor deve ser maior que zero
- ✅ Cliente deve existir no banco de dados
- ✅ Data de expiração (se fornecida) deve ser no futuro

#### Response (201 Created)

```json
{
  "code": "201 CREATED",
  "message": "Cupom promocional 'PROMO-2024-001' criado com sucesso",
  "data": [
    {
      "id": 1,
      "code": "PROMO-2024-001",
      "value": 100.00,
      "usedValue": 0.00,
      "availableValue": 100.00,
      "isActive": true,
      "expiresAt": "2024-12-31T23:59:59",
      "customerId": 1,
      "couponType": "PROMOTIONAL"
    }
  ]
}
```

#### Erros Possíveis

- **400 BAD_REQUEST**: 
  - "Cupom com código 'X' já existe"
  - "Cliente com ID X não encontrado"
  - "Data de expiração deve ser no futuro"
  - Validações de campos obrigatórios

#### Exemplo de Uso (cURL)

```bash
curl -X POST http://localhost:8080/coupons/promotional \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin_token>" \
  -d '{
    "code": "PROMO-2024-001",
    "value": 100.00,
    "customerId": 1,
    "expiresAt": "2024-12-31T23:59:59"
  }'
```

---

### 2. Listar Todos os Cupons (Admin)

**Endpoint**: `GET /coupons/all`  
**Permissão**: `ADMIN`  
**Autenticação**: Requerida

#### Response (200 OK)

```json
{
  "code": "200 OK",
  "message": "Cupons encontrados com sucesso",
  "data": [
    {
      "id": 1,
      "code": "PROMO-2024-001",
      "value": 100.00,
      "usedValue": 0.00,
      "availableValue": 100.00,
      "isActive": true,
      "expiresAt": "2024-12-31T23:59:59",
      "customerId": 1,
      "couponType": "PROMOTIONAL"
    },
    {
      "id": 2,
      "code": "TROCA-2024-001",
      "value": 50.00,
      "usedValue": 25.00,
      "availableValue": 25.00,
      "isActive": true,
      "expiresAt": null,
      "customerId": 2,
      "couponType": "EXCHANGE"
    }
  ]
}
```

---

### 3. Listar Cupons de um Cliente Específico (Admin)

**Endpoint**: `GET /coupons/customer/{customerId}`  
**Permissão**: `ADMIN`  
**Autenticação**: Requerida

#### Path Parameters

- `customerId` (Long): ID do cliente

#### Response (200 OK)

```json
{
  "code": "200 OK",
  "message": "Cupons do cliente 1 encontrados com sucesso",
  "data": [
    {
      "id": 1,
      "code": "PROMO-2024-001",
      "value": 100.00,
      "usedValue": 0.00,
      "availableValue": 100.00,
      "isActive": true,
      "expiresAt": "2024-12-31T23:59:59",
      "customerId": 1,
      "couponType": "PROMOTIONAL"
    }
  ]
}
```

#### Erros Possíveis

- **404 NOT_FOUND**: "Cliente com ID X não encontrado"

---

## 👤 Endpoints para Cliente

### 1. Listar Meus Cupons

**Endpoint**: `GET /coupons`  
**Permissão**: `USER`  
**Autenticação**: Requerida

Retorna todos os cupons (ativos e inativos) do cliente autenticado.

### 2. Listar Meus Cupons Ativos

**Endpoint**: `GET /coupons/active`  
**Permissão**: `USER`  
**Autenticação**: Requerida

Retorna apenas os cupons ativos e válidos (não expirados e com valor disponível) do cliente autenticado.

---

## 📝 Exemplos de Uso

### Cenário 1: Criar Cupom Promocional sem Expiração

```json
POST /coupons/promotional
{
  "code": "BLACK-FRIDAY-2024",
  "value": 200.00,
  "customerId": 5,
  "expiresAt": null
}
```

### Cenário 2: Criar Cupom Promocional com Expiração

```json
POST /coupons/promotional
{
  "code": "NATAL-2024",
  "value": 150.00,
  "customerId": 10,
  "expiresAt": "2024-12-25T23:59:59"
}
```

### Cenário 3: Criar Cupom para Múltiplos Clientes

Para criar cupons para múltiplos clientes, você precisa fazer uma requisição para cada cliente:

```javascript
const customers = [1, 2, 3, 4, 5];
const couponCode = "PROMO-2024-001";
const value = 50.00;
const expiresAt = "2024-12-31T23:59:59";

for (const customerId of customers) {
  await fetch('/coupons/promotional', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${adminToken}`
    },
    body: JSON.stringify({
      code: `${couponCode}-${customerId}`, // Código único por cliente
      value: value,
      customerId: customerId,
      expiresAt: expiresAt
    })
  });
}
```

---

## 🔄 Fluxo de Uso do Cupom

1. **Admin cria cupom promocional** → `POST /coupons/promotional`
2. **Cliente lista seus cupons** → `GET /coupons/active`
3. **Cliente usa cupom na compra** → `POST /orders` (incluindo o cupom no array `coupons`)
4. **Sistema valida e aplica desconto** → Desconto é aplicado ao total do pedido
5. **Sistema atualiza cupom** → `usedValue` é incrementado, cupom é desativado se totalmente usado

---

## ⚠️ Observações Importantes

1. **Código único**: Cada cupom deve ter um código único. Se tentar criar um cupom com código já existente, receberá erro.

2. **Valor disponível**: O cupom pode ser usado parcialmente. Por exemplo, um cupom de R$ 100,00 pode ser usado em R$ 30,00 em uma compra e ainda ter R$ 70,00 disponíveis.

3. **Desativação automática**: Quando `usedValue >= value`, o cupom é automaticamente desativado (`isActive = false`).

4. **Expiração**: Se o cupom tiver `expiresAt`, ele não pode ser usado após essa data, mesmo que ainda tenha valor disponível.

5. **Propriedade**: O cupom só pode ser usado pelo cliente ao qual foi atribuído. Tentar usar um cupom de outro cliente resultará em erro.

6. **Limite por compra**: Apenas 1 cupom promocional por pedido, mas pode combinar com múltiplos cupons de troca.

---

## 🧪 Testes Recomendados

### Testes para Admin

- [ ] Criar cupom promocional com sucesso
- [ ] Criar cupom sem data de expiração
- [ ] Criar cupom com data de expiração no futuro
- [ ] Tentar criar cupom com código duplicado (deve falhar)
- [ ] Tentar criar cupom para cliente inexistente (deve falhar)
- [ ] Tentar criar cupom com data de expiração no passado (deve falhar)
- [ ] Listar todos os cupons
- [ ] Listar cupons de um cliente específico

### Testes para Cliente

- [ ] Listar meus cupons
- [ ] Listar meus cupons ativos
- [ ] Usar cupom promocional em uma compra
- [ ] Usar cupom promocional parcialmente
- [ ] Tentar usar cupom expirado (deve falhar)
- [ ] Tentar usar cupom totalmente utilizado (deve falhar)
- [ ] Tentar usar 2 cupons promocionais na mesma compra (deve falhar)
- [ ] Usar 1 cupom promocional + múltiplos cupons de troca (deve funcionar)

---

## 📊 Estrutura de Dados

### Coupon Entity

```java
- id: Long
- code: String (único, max 50 caracteres)
- value: BigDecimal (valor total do cupom)
- usedValue: BigDecimal (valor já utilizado)
- isActive: Boolean (ativo/inativo)
- expiresAt: LocalDateTime (opcional, data de expiração)
- customer: Customer (cliente dono do cupom)
- exchange: Exchange (null para cupons promocionais)
- couponType: String ("EXCHANGE" ou "PROMOTIONAL")
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

### Métodos Úteis

- `getAvailableValue()`: Retorna `value - usedValue`
- `isValid()`: Retorna `true` se o cupom está ativo, não expirado e tem valor disponível

---

## 🔗 Relacionamento com Outros Módulos

### Módulo de Pedidos

Os cupons promocionais são usados no momento da criação do pedido:

```json
POST /orders
{
  "coupons": [
    {
      "couponCode": "PROMO-2024-001",
      "amountToUse": 50.00
    }
  ],
  // ... outros campos do pedido
}
```

### Módulo de Trocas

Cupons de troca são criados automaticamente quando uma troca é confirmada. Cupons promocionais são criados manualmente pelo admin.

---

**Última atualização**: 30/11/2024


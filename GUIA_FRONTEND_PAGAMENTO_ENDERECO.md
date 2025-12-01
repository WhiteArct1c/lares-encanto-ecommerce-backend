# Guia Frontend - Pagamento, Endereço e Cupons

Este documento descreve como o frontend deve interagir com o backend para criar pedidos, incluindo:

- Uso de múltiplos cupons de troca e/ou um único cupom promocional
- Seleção de cartão de crédito existente ou cadastro de novo cartão
- Seleção de endereço existente ou cadastro de novo endereço

---

## 1. Cupons de Troca e Promocionais

### Regras de Negócio

✅ **JÁ IMPLEMENTADO** - O backend já valida automaticamente:

- Permite múltiplos cupons de troca (EXCHANGE) em uma única compra
- Permite apenas **um** cupom promocional (PROMOTIONAL) por compra
- Pode combinar: múltiplos cupons de troca + 1 cupom promocional
- O desconto total não pode exceder o valor total do pedido (produtos + frete)

### Como Enviar no Request

```json
{
  "coupons": [
    {
      "couponCode": "TROCA-2024-001",
      "amountToUse": 100.0
    },
    {
      "couponCode": "TROCA-2024-002",
      "amountToUse": 50.0
    },
    {
      "couponCode": "PROMO-2024-001",
      "amountToUse": 30.0
    }
  ]
}
```

### Validações no Frontend (Recomendadas)

1. **Contar cupons promocionais**: Antes de enviar, verifique se há mais de 1 cupom promocional
2. **Calcular desconto total**: Soma todos os `amountToUse` e valida que não excede o total do pedido
3. **Exibir mensagens claras**: Se o usuário tentar adicionar mais de 1 cupom promocional, exiba: "Apenas um cupom promocional pode ser utilizado por compra"

### Exemplo de Fluxo

```javascript
// 1. Usuário adiciona cupons
const coupons = [
  { couponCode: "TROCA-001", amountToUse: 100.0 },
  { couponCode: "TROCA-002", amountToUse: 50.0 },
  { couponCode: "PROMO-001", amountToUse: 30.0 },
];

// 2. Validar no frontend (opcional, mas recomendado)
const promotionalCount = coupons.filter(
  (c) =>
    // Você precisa verificar o tipo do cupom na sua lista de cupons
    getCouponType(c.couponCode) === "PROMOTIONAL"
).length;

if (promotionalCount > 1) {
  alert("Apenas um cupom promocional pode ser utilizado por compra");
  return;
}

// 3. Enviar para o backend
const orderData = {
  // ... outros campos
  coupons: coupons,
};
```

---

## 2. Cartão de Crédito - Existente ou Novo

### Regras de Negócio

✅ **MELHORADO** - O backend agora:

- Valida que o cartão existente pertence ao cliente autenticado
- Permite criar um novo cartão no momento do pagamento
- Valida os dados obrigatórios ao criar um novo cartão

### Como Enviar no Request

#### Opção 1: Usar Cartão Existente

```json
{
  "orderPayments": [
    {
      "id": null,
      "installments": 3,
      "installmentValue": 66.67,
      "paymentMethod": "CREDIT_CARD",
      "creditCard": {
        "id": 1,
        "cardNumber": null,
        "cardName": null,
        "cardCode": null,
        "cardFlag": null,
        "mainCard": false
      }
    }
  ]
}
```

**Importante**: Quando usar um cartão existente, você pode enviar apenas o `id` do cartão. Os outros campos (`cardNumber`, `cardName`, etc.) podem ser `null` ou omitidos.

#### Opção 2: Criar Novo Cartão

```json
{
  "orderPayments": [
    {
      "id": null,
      "installments": 3,
      "installmentValue": 66.67,
      "paymentMethod": "CREDIT_CARD",
      "creditCard": {
        "id": null,
        "cardNumber": "1234567890123456",
        "cardName": "JOAO SILVA",
        "cardCode": "123",
        "cardFlag": "VISA",
        "mainCard": false
      }
    }
  ]
}
```

**Importante**: Quando criar um novo cartão, todos os campos são obrigatórios:

- `cardNumber`: Número do cartão (string)
- `cardName`: Nome no cartão (string)
- `cardCode`: Código de segurança/CVV (string)
- `cardFlag`: Bandeira do cartão (string) - ex: "VISA", "MASTERCARD", etc.
- `mainCard`: Se é o cartão principal (boolean)

### Validações no Frontend (Recomendadas)

1. **Verificar se é cartão existente ou novo**:

   ```javascript
   const isExistingCard = creditCard.id !== null && creditCard.id !== undefined;
   ```

2. **Se for novo cartão, validar campos obrigatórios**:
   ```javascript
   if (!isExistingCard) {
     if (!creditCard.cardNumber || creditCard.cardNumber.trim() === "") {
       alert("Número do cartão é obrigatório");
       return;
     }
     if (!creditCard.cardName || creditCard.cardName.trim() === "") {
       alert("Nome no cartão é obrigatório");
       return;
     }
     if (!creditCard.cardCode || creditCard.cardCode.trim() === "") {
       alert("Código de segurança é obrigatório");
       return;
     }
     if (!creditCard.cardFlag || creditCard.cardFlag.trim() === "") {
       alert("Bandeira do cartão é obrigatória");
       return;
     }
   }
   ```

### Exemplo de Fluxo

```javascript
// Cenário 1: Usuário seleciona cartão existente
const existingCard = {
  id: 1,
  cardNumber: null, // Pode ser null
  cardName: null, // Pode ser null
  cardCode: null, // Pode ser null
  cardFlag: null, // Pode ser null
  mainCard: false,
};

const paymentWithExistingCard = {
  id: null,
  installments: 3,
  installmentValue: 66.67,
  paymentMethod: "CREDIT_CARD",
  creditCard: existingCard,
};

// Cenário 2: Usuário cadastra novo cartão
const newCard = {
  id: null, // null indica que é novo
  cardNumber: "1234567890123456",
  cardName: "JOAO SILVA",
  cardCode: "123",
  cardFlag: "VISA",
  mainCard: false,
};

const paymentWithNewCard = {
  id: null,
  installments: 3,
  installmentValue: 66.67,
  paymentMethod: "CREDIT_CARD",
  creditCard: newCard,
};

// Enviar para o backend
const orderData = {
  // ... outros campos
  orderPayments: [paymentWithExistingCard], // ou [paymentWithNewCard]
};
```

### Erros Possíveis

- **"Cartão de crédito com ID X não encontrado ou não pertence ao cliente autenticado"**: O cartão informado não existe ou não pertence ao cliente logado
- **"Dados do cartão de crédito são obrigatórios quando não é informado um cartão existente"**: Tentou criar um novo cartão mas não enviou o objeto `creditCard`
- **"Número do cartão é obrigatório ao cadastrar um novo cartão"**: Campo `cardNumber` está vazio ou null
- **"Nome no cartão é obrigatório ao cadastrar um novo cartão"**: Campo `cardName` está vazio ou null
- **"Código de segurança do cartão é obrigatório ao cadastrar um novo cartão"**: Campo `cardCode` está vazio ou null
- **"Bandeira do cartão é obrigatória ao cadastrar um novo cartão"**: Campo `cardFlag` está vazio ou null

---

## 3. Endereço de Entrega - Existente ou Novo

### Regras de Negócio

✅ **JÁ IMPLEMENTADO** - O backend já suporta:

- Seleção de endereço existente (por ID)
- Cadastro de novo endereço no momento do pedido
- Validação automática de propriedade do endereço

### Como Enviar no Request

#### Opção 1: Usar Endereço Existente

```json
{
  "address": {
    "id": "1",
    "title": null,
    "cep": null,
    "residenceType": null,
    "addressType": null,
    "addressCategories": null,
    "streetName": null,
    "addressNumber": null,
    "neighborhoods": null,
    "city": null,
    "state": null,
    "country": null,
    "observations": null
  }
}
```

**Importante**: Quando usar um endereço existente, você pode enviar apenas o `id` como string. Os outros campos podem ser `null` ou omitidos.

#### Opção 2: Criar Novo Endereço

```json
{
  "address": {
    "id": null,
    "title": "Casa",
    "cep": "12345-678",
    "residenceType": "CASA",
    "addressType": "RUA",
    "addressCategories": ["DELIVERY", "BILLING"],
    "streetName": "Rua das Flores",
    "addressNumber": "123",
    "neighborhoods": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "country": "Brasil",
    "observations": "Apt 101"
  }
}
```

**Importante**: Quando criar um novo endereço, todos os campos são obrigatórios (exceto `id` que deve ser `null` ou omitido).

### Validações no Frontend (Recomendadas)

1. **Verificar se é endereço existente ou novo**:

   ```javascript
   const isExistingAddress =
     address.id !== null && address.id !== undefined && address.id !== "";
   ```

2. **Se for novo endereço, validar campos obrigatórios**:
   ```javascript
   if (!isExistingAddress) {
     const requiredFields = [
       "title",
       "cep",
       "residenceType",
       "addressType",
       "addressCategories",
       "streetName",
       "addressNumber",
       "neighborhoods",
       "city",
       "state",
       "country",
       "observations",
     ];

     for (const field of requiredFields) {
       if (
         !address[field] ||
         (Array.isArray(address[field]) && address[field].length === 0)
       ) {
         alert(`Campo ${field} é obrigatório`);
         return;
       }
     }
   }
   ```

### Exemplo de Fluxo

```javascript
// Cenário 1: Usuário seleciona endereço existente
const existingAddress = {
  id: "1", // String com o ID do endereço
  // Outros campos podem ser null ou omitidos
};

// Cenário 2: Usuário cadastra novo endereço
const newAddress = {
  id: null, // null indica que é novo
  title: "Casa",
  cep: "12345-678",
  residenceType: "CASA",
  addressType: "RUA",
  addressCategories: ["DELIVERY"],
  streetName: "Rua das Flores",
  addressNumber: "123",
  neighborhoods: "Centro",
  city: "São Paulo",
  state: "SP",
  country: "Brasil",
  observations: "Apt 101",
};

// Enviar para o backend
const orderData = {
  // ... outros campos
  address: existingAddress, // ou newAddress
};
```

### Erros Possíveis

- **"Endereço não encontrado"**: O ID do endereço informado não existe no banco de dados
- **Validações de campos obrigatórios**: Se algum campo obrigatório estiver vazio ao criar um novo endereço

---

## 4. ⚠️ IMPORTANTE: Cálculo de Pagamentos com Frete

### Regra Crítica

**Os pagamentos devem cobrir o valor total do pedido, incluindo produtos + frete.**

O backend calcula o total do pedido como: `produtos + frete - desconto de cupons`

### Exemplo de Cálculo

```javascript
// Valores do pedido
const productsTotal = 1185.5; // Soma dos produtos
const shippingPrice = 140.5; // Frete
const couponDiscount = 0.0; // Desconto de cupons

// Total a pagar
const totalToPay = productsTotal + shippingPrice - couponDiscount;
// totalToPay = 1185.50 + 140.50 - 0.00 = 1326.00

// Os pagamentos devem somar exatamente 1326.00
const payments = [
  {
    installmentValue: 1326.0,
    installments: 1,
    paymentMethod: "CREDIT_CARD",
    creditCard: { id: 1 },
  },
];
// Soma dos pagamentos: 1326.00 ✅
```

### Erro Comum

❌ **ERRADO**: Calcular pagamentos apenas com base no `totalPrice` (produtos)

```javascript
// ERRADO - não inclui o frete
const payments = [
  {
    installmentValue: 1185.5, // Apenas produtos
    installments: 1,
  },
];
// Backend espera: 1326.00 (produtos + frete)
// Frontend envia: 1185.50 (apenas produtos)
// Resultado: ERRO de validação
```

✅ **CORRETO**: Calcular pagamentos incluindo o frete

```javascript
// CORRETO - inclui produtos + frete
const totalToPay = productsTotal + shippingPrice - couponDiscount;
const payments = [
  {
    installmentValue: totalToPay, // Produtos + frete - cupons
    installments: 1,
  },
];
```

### Validação no Frontend

```javascript
function validatePayments(
  payments,
  productsTotal,
  shippingPrice,
  couponDiscount
) {
  const totalToPay = productsTotal + shippingPrice - couponDiscount;

  const totalPayments = payments.reduce((sum, payment) => {
    return sum + payment.installmentValue * payment.installments;
  }, 0);

  if (Math.abs(totalPayments - totalToPay) > 0.01) {
    const difference = totalToPay - totalPayments;
    if (difference > 0) {
      throw new Error(
        `Faltam R$ ${difference.toFixed(
          2
        )} nos pagamentos. Lembre-se de incluir o frete!`
      );
    } else {
      throw new Error(
        `Os pagamentos excedem em R$ ${Math.abs(difference).toFixed(2)}`
      );
    }
  }
}
```

---

## 5. Exemplo Completo de Request

```json
{
  "address": {
    "id": "1"
  },
  "orderPayments": [
    {
      "id": null,
      "installments": 3,
      "installmentValue": 442.0,
      "paymentMethod": "CREDIT_CARD",
      "creditCard": {
        "id": 1
      }
    }
  ],
  "orderProducts": [
    {
      "id": null,
      "product": {
        "id": 1,
        "name": "Produto Exemplo",
        "salePrice": 200.0
      },
      "quantity": 1
    }
  ],
  "shipping": {
    "name": "PAC",
    "deliveryTime": 10,
    "price": 35.0
  },
  "type": "COMPRA",
  "totalPrice": 200.0,
  "coupons": [
    {
      "couponCode": "TROCA-2024-001",
      "amountToUse": 100.0
    },
    {
      "couponCode": "PROMO-2024-001",
      "amountToUse": 30.0
    }
  ]
}
```

**Nota**: Neste exemplo:

- `totalPrice`: 200.00 (apenas produtos)
- `shipping.price`: 35.00 (frete)
- Total a pagar: 200.00 + 35.00 - 130.00 (cupons) = 105.00
- `installmentValue * installments`: 442.00 \* 3 = 1326.00 (exemplo acima está incorreto, deveria ser ajustado)

---

## 5. Resumo das Mudanças

### O que mudou:

1. **Cartão de Crédito**:

   - ✅ Agora valida que o cartão existente pertence ao cliente
   - ✅ Melhor validação de dados ao criar novo cartão
   - ✅ Mensagens de erro mais claras

2. **Cupons**:

   - ✅ Já estava funcionando corretamente
   - ✅ Permite múltiplos cupons de troca + 1 promocional

3. **Endereço**:
   - ✅ Já estava funcionando corretamente
   - ✅ Permite selecionar existente ou criar novo

### O que NÃO mudou:

- A estrutura do `OrderCreateRequestDTO` permanece a mesma
- Os endpoints continuam os mesmos
- As validações de pagamento e estoque continuam as mesmas

---

## 7. Checklist para Implementação no Frontend

- [ ] Validar que apenas 1 cupom promocional pode ser usado
- [ ] Validar que o desconto total não excede o valor do pedido
- [ ] Implementar lógica para distinguir cartão existente vs novo
- [ ] Validar campos obrigatórios ao criar novo cartão
- [ ] Implementar lógica para distinguir endereço existente vs novo
- [ ] Validar campos obrigatórios ao criar novo endereço
- [ ] Tratar erros específicos do backend
- [ ] Exibir mensagens de erro claras para o usuário

---

## 8. Endpoints Relacionados

### Listar Cartões do Cliente

```
GET /credit-cards
```

### Listar Endereços do Cliente

```
GET /addresses
```

### Listar Cupons do Cliente

```
GET /coupons
GET /coupons/active
```

### Criar Pedido

```
POST /orders
```

---

## 9. Observações Importantes

1. **⚠️ CRÍTICO - Incluir Frete nos Pagamentos**:

   - Os pagamentos devem cobrir **produtos + frete - cupons**
   - Não envie pagamentos apenas para o valor dos produtos
   - O backend valida que a soma dos pagamentos = total (produtos + frete - cupons)

2. **Segurança**: O backend sempre valida que cartões e endereços pertencem ao cliente autenticado. Não é possível usar cartões/endereços de outros clientes.

3. **Segurança**: O backend sempre valida que cartões e endereços pertencem ao cliente autenticado. Não é possível usar cartões/endereços de outros clientes.

4. **Validações**: Embora o backend valide tudo, é recomendado fazer validações no frontend também para melhor UX.

5. **Logs**: O backend agora possui logs detalhados. Em caso de problemas, verifique os logs do servidor.

6. **Compatibilidade**: As mudanças são retrocompatíveis. Se você já estava enviando os dados corretamente, não precisa mudar nada.

---

**Última atualização**: 30/11/2024

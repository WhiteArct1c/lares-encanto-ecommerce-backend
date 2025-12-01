# Guia Frontend - Cupons Promocionais

## 📋 Visão Geral

Cupons promocionais são válidos para **qualquer cliente** e são criados pelo admin para campanhas e promoções do site. O frontend precisa validar se um cupom promocional é válido antes de permitir que o usuário o use na compra.

---

## 🔍 Validação de Cupom Promocional

### Endpoint de Validação

**Endpoint**: `GET /coupons/validate/{couponCode}`  
**Permissão**: **Público** (não requer autenticação)  
**Método**: GET

#### Request

```
GET /coupons/validate/BLACK-FRIDAY-2024
```

#### Response (200 OK) - Cupom Válido

```json
{
  "code": "200 OK",
  "message": "Cupom promocional válido",
  "data": [
    {
      "id": 1,
      "code": "BLACK-FRIDAY-2024",
      "value": 100.0,
      "usedValue": 0.0,
      "availableValue": 100.0,
      "isActive": true,
      "expiresAt": "2024-12-31T23:59:59",
      "customerId": null,
      "couponType": "PROMOTIONAL"
    }
  ]
}
```

#### Response (400/404) - Cupom Inválido

```json
{
  "code": "400 BAD_REQUEST",
  "message": "Cupom expirado",
  "description": null
}
```

#### Possíveis Erros

- **404 NOT_FOUND**: "Cupom não encontrado" - Código do cupom não existe
- **400 BAD_REQUEST**: "Este cupom não é promocional" - Cupom é de troca, não promocional
- **400 BAD_REQUEST**: "Cupom já foi utilizado e está inativo" - Cupom foi totalmente usado
- **400 BAD_REQUEST**: "Cupom expirado" - Data de expiração passou
- **400 BAD_REQUEST**: "Cupom não possui valor disponível" - Valor já foi totalmente utilizado

---

## 💻 Implementação no Frontend

### 1. Função para Validar Cupom

```typescript
interface CouponValidationResponse {
  code: string;
  message: string;
  data: Array<{
    id: number;
    code: string;
    value: number;
    usedValue: number;
    availableValue: number;
    isActive: boolean;
    expiresAt: string | null;
    customerId: number | null;
    couponType: string;
  }>;
}

async function validatePromotionalCoupon(
  couponCode: string
): Promise<CouponValidationResponse> {
  try {
    const response = await fetch(`/api/coupons/validate/${couponCode}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    const data: CouponValidationResponse = await response.json();

    if (!response.ok) {
      throw new Error(data.message || "Erro ao validar cupom");
    }

    return data;
  } catch (error) {
    throw error;
  }
}
```

### 2. Componente de Input de Cupom

```tsx
import { useState } from "react";

interface CouponInputProps {
  onCouponValidated: (coupon: CouponData) => void;
  onCouponRemoved: () => void;
}

interface CouponData {
  code: string;
  availableValue: number;
  value: number;
}

function CouponInput({ onCouponValidated, onCouponRemoved }: CouponInputProps) {
  const [couponCode, setCouponCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [validatedCoupon, setValidatedCoupon] = useState<CouponData | null>(
    null
  );

  const handleValidateCoupon = async () => {
    if (!couponCode.trim()) {
      setError("Digite o código do cupom");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await validatePromotionalCoupon(
        couponCode.toUpperCase()
      );

      if (response.data && response.data.length > 0) {
        const coupon = response.data[0];

        // Verifica se é promocional
        if (coupon.couponType !== "PROMOTIONAL") {
          setError("Este cupom não é promocional");
          return;
        }

        const couponData: CouponData = {
          code: coupon.code,
          availableValue: coupon.availableValue,
          value: coupon.value,
        };

        setValidatedCoupon(couponData);
        onCouponValidated(couponData);
      }
    } catch (err: any) {
      setError(err.message || "Erro ao validar cupom");
      setValidatedCoupon(null);
      onCouponRemoved();
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveCoupon = () => {
    setCouponCode("");
    setValidatedCoupon(null);
    setError(null);
    onCouponRemoved();
  };

  return (
    <div className="coupon-input">
      {!validatedCoupon ? (
        <>
          <div className="input-group">
            <input
              type="text"
              value={couponCode}
              onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
              placeholder="Digite o código do cupom"
              disabled={loading}
              onKeyPress={(e) => {
                if (e.key === "Enter") {
                  handleValidateCoupon();
                }
              }}
            />
            <button
              onClick={handleValidateCoupon}
              disabled={loading || !couponCode.trim()}
            >
              {loading ? "Validando..." : "Validar"}
            </button>
          </div>
          {error && <div className="error-message">{error}</div>}
        </>
      ) : (
        <div className="coupon-validated">
          <div className="coupon-info">
            <span className="coupon-code">Cupom: {validatedCoupon.code}</span>
            <span className="coupon-value">
              Desconto disponível: R${" "}
              {validatedCoupon.availableValue.toFixed(2)}
            </span>
          </div>
          <button onClick={handleRemoveCoupon} className="remove-coupon">
            Remover
          </button>
        </div>
      )}
    </div>
  );
}
```

### 3. Integração no Checkout

```tsx
function Checkout() {
  const [promotionalCoupon, setPromotionalCoupon] = useState<CouponData | null>(
    null
  );
  const [exchangeCoupons, setExchangeCoupons] = useState<CouponData[]>([]);
  const [orderTotal, setOrderTotal] = useState(0);

  const handlePromotionalCouponValidated = (coupon: CouponData) => {
    // Remove cupom promocional anterior se houver
    setPromotionalCoupon(coupon);
  };

  const handlePromotionalCouponRemoved = () => {
    setPromotionalCoupon(null);
  };

  const calculateDiscount = () => {
    let discount = 0;

    // Cupom promocional (apenas 1)
    if (promotionalCoupon) {
      discount += Math.min(promotionalCoupon.availableValue, orderTotal);
    }

    // Cupons de troca (múltiplos)
    exchangeCoupons.forEach((coupon) => {
      discount += Math.min(coupon.availableValue, orderTotal - discount);
    });

    return Math.min(discount, orderTotal);
  };

  const finalTotal = orderTotal - calculateDiscount();

  const handleCreateOrder = async () => {
    const coupons: Array<{ couponCode: string; amountToUse: number }> = [];

    // Adiciona cupom promocional (se houver)
    if (promotionalCoupon) {
      const amountToUse = Math.min(
        promotionalCoupon.availableValue,
        orderTotal
      );
      coupons.push({
        couponCode: promotionalCoupon.code,
        amountToUse: amountToUse,
      });
    }

    // Adiciona cupons de troca
    exchangeCoupons.forEach((coupon) => {
      const remainingTotal =
        orderTotal - coupons.reduce((sum, c) => sum + c.amountToUse, 0);
      if (remainingTotal > 0) {
        const amountToUse = Math.min(coupon.availableValue, remainingTotal);
        coupons.push({
          couponCode: coupon.code,
          amountToUse: amountToUse,
        });
      }
    });

    const orderData = {
      // ... outros campos
      coupons: coupons,
    };

    // Enviar para POST /orders
  };

  return (
    <div className="checkout">
      {/* ... outros componentes */}

      <div className="coupons-section">
        <h3>Cupons de Desconto</h3>

        {/* Cupom Promocional */}
        <div className="promotional-coupon">
          <label>Cupom Promocional</label>
          <CouponInput
            onCouponValidated={handlePromotionalCouponValidated}
            onCouponRemoved={handlePromotionalCouponRemoved}
          />
          {promotionalCoupon && (
            <div className="coupon-applied">
              ✓ Cupom {promotionalCoupon.code} aplicado
            </div>
          )}
        </div>

        {/* Cupons de Troca (do cliente) */}
        <div className="exchange-coupons">
          <label>Meus Cupons de Troca</label>
          {/* Listar cupons de troca do cliente */}
          {/* GET /coupons/active */}
        </div>
      </div>

      <div className="order-summary">
        <div>Subtotal: R$ {orderTotal.toFixed(2)}</div>
        <div>Desconto: R$ {calculateDiscount().toFixed(2)}</div>
        <div className="total">Total: R$ {finalTotal.toFixed(2)}</div>
      </div>

      <button onClick={handleCreateOrder}>Finalizar Compra</button>
    </div>
  );
}
```

---

## ✅ Validações Recomendadas no Frontend

### 1. Antes de Validar

```typescript
function validateCouponCodeInput(code: string): string | null {
  if (!code || code.trim().length === 0) {
    return "Código do cupom é obrigatório";
  }

  if (code.length > 50) {
    return "Código do cupom muito longo";
  }

  return null;
}
```

### 2. Após Validação Bem-Sucedida

```typescript
function validateCouponForOrder(
  coupon: CouponData,
  orderTotal: number
): string | null {
  // Verifica se é promocional
  if (coupon.couponType !== "PROMOTIONAL") {
    return "Este cupom não é promocional";
  }

  // Verifica se tem valor disponível
  if (coupon.availableValue <= 0) {
    return "Cupom não possui valor disponível";
  }

  // Verifica se não expirou (se tiver data)
  if (coupon.expiresAt) {
    const expirationDate = new Date(coupon.expiresAt);
    if (expirationDate < new Date()) {
      return "Cupom expirado";
    }
  }

  return null;
}
```

### 3. Limite de Cupons Promocionais

```typescript
// Apenas 1 cupom promocional por pedido
if (promotionalCoupon && newCoupon.couponType === "PROMOTIONAL") {
  return "Você já possui um cupom promocional aplicado. Apenas um cupom promocional por compra.";
}
```

---

## 🔄 Fluxo Completo

### 1. Usuário Digita Código do Cupom

```
Usuário digita: "BLACK-FRIDAY-2024"
```

### 2. Frontend Valida o Cupom

```typescript
const response = await validatePromotionalCoupon("BLACK-FRIDAY-2024");
```

### 3. Backend Retorna Informações

```json
{
  "code": "200 OK",
  "message": "Cupom promocional válido",
  "data": [
    {
      "code": "BLACK-FRIDAY-2024",
      "availableValue": 100.0,
      "value": 100.0,
      "couponType": "PROMOTIONAL"
    }
  ]
}
```

### 4. Frontend Aplica o Desconto

```typescript
// Calcula desconto
const discount = Math.min(coupon.availableValue, orderTotal);
const finalTotal = orderTotal - discount;
```

### 5. Usuário Finaliza Compra

```typescript
// Envia para o backend
const orderData = {
  coupons: [
    {
      couponCode: "BLACK-FRIDAY-2024",
      amountToUse: discount,
    },
  ],
  // ... outros campos
};
```

### 6. Backend Valida e Processa

O backend valida novamente o cupom e aplica o desconto no pedido.

---

## 📊 Exemplo de UI

```
┌─────────────────────────────────────────┐
│ Cupons de Desconto                      │
├─────────────────────────────────────────┤
│                                         │
│ Cupom Promocional:                      │
│ ┌───────────────────────────────────┐ │
│ │ [BLACK-FRIDAY-2024] [Validar]     │ │
│ └───────────────────────────────────┘ │
│                                         │
│ ✓ Cupom BLACK-FRIDAY-2024 aplicado     │
│   Desconto disponível: R$ 100,00        │
│   [Remover]                             │
│                                         │
│ Meus Cupons de Troca:                   │
│ ┌───────────────────────────────────┐ │
│ │ ☑ TROCA-2024-001 (R$ 50,00)      │ │
│ │ ☐ TROCA-2024-002 (R$ 30,00)      │ │
│ └───────────────────────────────────┘ │
│                                         │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ Resumo do Pedido                        │
├─────────────────────────────────────────┤
│ Subtotal:              R$ 500,00        │
│ Desconto (cupons):     R$ 150,00        │
│ ─────────────────────────────────────── │
│ Total:                 R$ 350,00        │
└─────────────────────────────────────────┘
```

---

## ⚠️ Observações Importantes

1. **Validação Dupla**: O frontend deve validar antes de mostrar ao usuário, mas o backend também valida na criação do pedido. Não confie apenas na validação do frontend.

2. **Cupons Promocionais são Públicos**: Qualquer cliente pode usar qualquer cupom promocional válido. Não há restrição de propriedade.

3. **Apenas 1 Promocional**: Apenas um cupom promocional por pedido, mas pode combinar com múltiplos cupons de troca.

4. **Valor Disponível**: O cupom pode ter mais valor disponível do que o total do pedido. Use apenas o necessário.

5. **Expiração**: Se o cupom tiver `expiresAt`, verifique se não expirou antes de permitir o uso.

6. **Endpoint Público**: O endpoint de validação não requer autenticação, então pode ser chamado antes do usuário fazer login.

---

## 🧪 Testes Recomendados

- [ ] Validar cupom promocional válido
- [ ] Validar cupom promocional expirado
- [ ] Validar cupom promocional totalmente utilizado
- [ ] Validar cupom de troca (deve retornar erro)
- [ ] Validar cupom inexistente
- [ ] Aplicar cupom promocional no checkout
- [ ] Aplicar cupom promocional + cupons de troca
- [ ] Tentar aplicar 2 cupons promocionais (deve bloquear)
- [ ] Remover cupom promocional aplicado
- [ ] Validar cupom sem data de expiração

---

## 📝 Checklist de Implementação

- [ ] Criar função `validatePromotionalCoupon()`
- [ ] Criar componente `CouponInput`
- [ ] Integrar validação no checkout
- [ ] Exibir informações do cupom validado
- [ ] Calcular desconto corretamente
- [ ] Validar limite de 1 cupom promocional
- [ ] Tratar erros de validação
- [ ] Permitir remover cupom aplicado
- [ ] Atualizar total do pedido ao aplicar cupom
- [ ] Enviar cupom no request de criação do pedido

---

**Última atualização**: 30/11/2024

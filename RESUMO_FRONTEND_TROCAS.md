# Resumo: Como o Frontend Usa os Endpoints de Trocas/Devoluções

## 📋 Visão Geral

O sistema permite que clientes solicitem trocas/devoluções de **um ou mais produtos** de um pedido entregue, e que administradores gerenciem todo o fluxo.

---

## 🔐 Autenticação

**Todos os endpoints requerem token JWT:**
```
Authorization: Bearer {token}
```

---

## 👤 Endpoints para Cliente

### 1. Solicitar Troca/Devolução de Múltiplos Produtos

**Endpoint:** `POST /exchanges`

**Request Body:**
```json
{
  "orderId": 1,
  "items": [
    {
      "orderProductId": 10,
      "quantity": 2,
      "reason": "Produto com defeito"
    },
    {
      "orderProductId": 11,
      "quantity": 1,
      "reason": "Não atendeu expectativas"
    }
  ]
}
```

**Exemplo JavaScript:**
```javascript
async function requestExchange(orderId, items) {
  const response = await fetch('/exchanges', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      orderId: orderId,
      items: items
    })
  });
  
  return await response.json();
}

// Uso:
requestExchange(1, [
  { orderProductId: 10, quantity: 2, reason: "Defeito" },
  { orderProductId: 11, quantity: 1, reason: "Não gostei" }
]);
```

**Response (201 Created):**
```json
{
  "status": "201 CREATED",
  "message": "Troca/devolução de 2 item(ns) solicitada com sucesso",
  "data": [
    {
      "id": 1,
      "orderId": 1,
      "orderProduct": {...},
      "quantity": 2,
      "status": {"name": "TROCA SOLICITADA"},
      "couponGenerated": false
    },
    {
      "id": 2,
      "orderId": 1,
      "orderProduct": {...},
      "quantity": 1,
      "status": {"name": "TROCA SOLICITADA"},
      "couponGenerated": false
    }
  ]
}
```

---

### 2. Listar Minhas Trocas

**Endpoint:** `GET /exchanges/my-exchanges`

**Exemplo JavaScript:**
```javascript
async function getMyExchanges() {
  const response = await fetch('/exchanges/my-exchanges', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
}
```

**Response:**
```json
{
  "status": "200 OK",
  "message": "Trocas encontradas com sucesso",
  "data": [
    {
      "id": 1,
      "orderId": 1,
      "orderProduct": {
        "id": 10,
        "quantity": 3,
        "product": {
          "id": 5,
          "name": "Cadeira de Cozinha Moderna",
          "salePrice": 300.00
        }
      },
      "quantity": 2,
      "reason": "Produto com defeito",
      "status": {
        "id": 8,
        "name": "TROCA ACEITA"
      },
      "couponGenerated": false,
      "createdAt": "2024-11-30T10:00:00"
    }
  ]
}
```

---

### 3. Listar Minhas Trocas (Alternativa)

**Endpoint:** `GET /exchanges?customerId={meuId}`

**Nota:** Cliente só pode usar seu próprio ID. Se tentar outro ID, retorna erro.

**Exemplo JavaScript:**
```javascript
async function getMyExchangesById(customerId) {
  const response = await fetch(`/exchanges?customerId=${customerId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
}
```

---

## 👨‍💼 Endpoints para Admin

### 1. Listar Todas as Trocas

**Endpoint:** `GET /exchanges/all`

**Exemplo JavaScript:**
```javascript
async function getAllExchanges() {
  const response = await fetch('/exchanges/all', {
    headers: {
      'Authorization': `Bearer ${adminToken}`
    }
  });
  
  return await response.json();
}
```

---

### 2. Listar Trocas de um Cliente Específico

**Endpoint:** `GET /exchanges?customerId={id}`

**Exemplo JavaScript:**
```javascript
async function getExchangesByCustomer(customerId) {
  const response = await fetch(`/exchanges?customerId=${customerId}`, {
    headers: {
      'Authorization': `Bearer ${adminToken}`
    }
  });
  
  return await response.json();
}
```

---

### 3. Listar Trocas Pendentes

**Endpoint:** `GET /exchanges/pending`

**Exemplo JavaScript:**
```javascript
async function getPendingExchanges() {
  const response = await fetch('/exchanges/pending', {
    headers: {
      'Authorization': `Bearer ${adminToken}`
    }
  });
  
  return await response.json();
}
```

---

### 4. Autorizar ou Recusar Troca

**Endpoint:** `PUT /exchanges/authorize`

**Request Body:**
```json
{
  "exchangeId": 1,
  "action": "APPROVE"  // ou "REJECT"
}
```

**Exemplo JavaScript:**
```javascript
async function authorizeExchange(exchangeId, approve) {
  const response = await fetch('/exchanges/authorize', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${adminToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      exchangeId: exchangeId,
      action: approve ? "APPROVE" : "REJECT"
    })
  });
  
  return await response.json();
}
```

---

### 5. Confirmar Recebimento

**Endpoint:** `PUT /exchanges/confirm-receipt`

**Request Body:**
```json
{
  "exchangeId": 1,
  "returnToStock": true
}
```

**Exemplo JavaScript:**
```javascript
async function confirmReceipt(exchangeId, returnToStock) {
  const response = await fetch('/exchanges/confirm-receipt', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${adminToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      exchangeId: exchangeId,
      returnToStock: returnToStock
    })
  });
  
  return await response.json();
}
```

**Nota:** Após confirmação, o cupom é gerado automaticamente.

---

### 6. Atualizar Status Diretamente

**Endpoint:** `PUT /exchanges/status`

**Request Body:**
```json
{
  "exchangeId": 1,
  "statusName": "TROCA CONCLUÍDA"
}
```

**Exemplo JavaScript:**
```javascript
async function updateExchangeStatus(exchangeId, statusName) {
  const response = await fetch('/exchanges/status', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${adminToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      exchangeId: exchangeId,
      statusName: statusName
    })
  });
  
  return await response.json();
}
```

**Status válidos:**
- `TROCA SOLICITADA`
- `TROCA ACEITA`
- `TROCA RECUSADA`
- `TROCA CONCLUÍDA`

---

## 🎨 Fluxo Completo no Frontend

### Tela: "Meus Pedidos" (Cliente)

```javascript
// 1. Lista pedidos entregues
const orders = await fetch('/orders').then(r => r.json());
const deliveredOrders = orders.data.filter(
  o => o.status.name === 'ENTREGUE'
);

// 2. Para cada pedido, mostra botão "Solicitar Troca"
deliveredOrders.forEach(order => {
  // Botão abre modal de troca
  showExchangeModal(order);
});
```

---

### Modal: "Solicitar Troca" (Cliente)

```javascript
function ExchangeModal({ order }) {
  const [selectedItems, setSelectedItems] = useState([]);

  // Inicializa com produtos do pedido
  const [items, setItems] = useState(
    order.orderProducts.map(op => ({
      orderProductId: op.id,
      quantity: 0,
      reason: '',
      selected: false,
      maxQuantity: op.quantity,
      productName: op.product.name
    }))
  );

  const handleSubmit = async () => {
    // Filtra apenas itens selecionados com quantidade > 0
    const itemsToSend = items
      .filter(item => item.selected && item.quantity > 0)
      .map(item => ({
        orderProductId: item.orderProductId,
        quantity: item.quantity,
        reason: item.reason || null
      }));

    const response = await fetch('/exchanges', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        orderId: order.id,
        items: itemsToSend
      })
    });

    if (response.ok) {
      alert('Troca solicitada com sucesso!');
      // Redireciona para "Minhas Trocas"
    }
  };

  return (
    <div>
      <h2>Solicitar Troca/Devolução</h2>
      {items.map((item, index) => (
        <div key={item.orderProductId}>
          <input
            type="checkbox"
            checked={item.selected}
            onChange={() => toggleItem(index)}
          />
          <span>{item.productName} (Comprado: {item.maxQuantity})</span>
          
          {item.selected && (
            <>
              <input
                type="number"
                min="1"
                max={item.maxQuantity}
                value={item.quantity}
                onChange={(e) => updateQuantity(index, e.target.value)}
              />
              <textarea
                placeholder="Motivo (opcional)"
                value={item.reason}
                onChange={(e) => updateReason(index, e.target.value)}
              />
            </>
          )}
        </div>
      ))}
      <button onClick={handleSubmit}>Solicitar Troca</button>
    </div>
  );
}
```

---

### Tela: "Minhas Trocas" (Cliente)

```javascript
function MyExchangesPage() {
  const [exchanges, setExchanges] = useState([]);

  useEffect(() => {
    fetch('/exchanges/my-exchanges', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(r => r.json())
      .then(data => setExchanges(data.data));
  }, []);

  return (
    <div>
      <h1>Minhas Trocas/Devoluções</h1>
      {exchanges.map(exchange => (
        <div key={exchange.id}>
          <h3>Produto: {exchange.orderProduct.product.name}</h3>
          <p>Quantidade: {exchange.quantity}</p>
          <p>Status: {exchange.status.name}</p>
          {exchange.couponGenerated && (
            <p>✅ Cupom gerado! Verifique seus cupons.</p>
          )}
        </div>
      ))}
    </div>
  );
}
```

---

### Tela: "Gerenciar Trocas" (Admin)

```javascript
function AdminExchangesPage() {
  const [exchanges, setExchanges] = useState([]);
  const [filter, setFilter] = useState('all'); // all, pending, byCustomer

  useEffect(() => {
    let endpoint = '/exchanges/all';
    
    if (filter === 'pending') {
      endpoint = '/exchanges/pending';
    } else if (filter === 'byCustomer') {
      const customerId = prompt('ID do cliente:');
      endpoint = `/exchanges?customerId=${customerId}`;
    }

    fetch(endpoint, {
      headers: { 'Authorization': `Bearer ${adminToken}` }
    })
      .then(r => r.json())
      .then(data => setExchanges(data.data));
  }, [filter]);

  const handleAuthorize = async (exchangeId, approve) => {
    await fetch('/exchanges/authorize', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${adminToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        exchangeId: exchangeId,
        action: approve ? 'APPROVE' : 'REJECT'
      })
    });
    // Recarrega lista
  };

  const handleConfirmReceipt = async (exchangeId, returnToStock) => {
    await fetch('/exchanges/confirm-receipt', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${adminToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        exchangeId: exchangeId,
        returnToStock: returnToStock
      })
    });
    // Recarrega lista
  };

  return (
    <div>
      <h1>Gerenciar Trocas</h1>
      <select onChange={(e) => setFilter(e.target.value)}>
        <option value="all">Todas</option>
        <option value="pending">Pendentes</option>
        <option value="byCustomer">Por Cliente</option>
      </select>

      {exchanges.map(exchange => (
        <div key={exchange.id}>
          <h3>{exchange.orderProduct.product.name}</h3>
          <p>Cliente: {exchange.order.orderId}</p>
          <p>Status: {exchange.status.name}</p>
          
          {exchange.status.name === 'TROCA SOLICITADA' && (
            <>
              <button onClick={() => handleAuthorize(exchange.id, true)}>
                Autorizar
              </button>
              <button onClick={() => handleAuthorize(exchange.id, false)}>
                Recusar
              </button>
            </>
          )}

          {exchange.status.name === 'TROCA ACEITA' && (
            <>
              <label>
                <input
                  type="checkbox"
                  onChange={(e) => setReturnToStock(e.target.checked)}
                />
                Retornar ao estoque
              </label>
              <button onClick={() => handleConfirmReceipt(exchange.id, returnToStock)}>
                Confirmar Recebimento
              </button>
            </>
          )}
        </div>
      ))}
    </div>
  );
}
```

---

## 📊 Resumo dos Endpoints

| Método | Endpoint | Role | Descrição |
|--------|----------|------|-----------|
| `POST` | `/exchanges` | Cliente | Solicitar troca de múltiplos produtos |
| `GET` | `/exchanges/my-exchanges` | Cliente | Listar minhas trocas |
| `GET` | `/exchanges?customerId={id}` | Cliente/Admin | Cliente: só próprio ID<br>Admin: qualquer ID |
| `GET` | `/exchanges/all` | Admin | Listar todas as trocas |
| `GET` | `/exchanges/pending` | Admin | Listar trocas pendentes |
| `PUT` | `/exchanges/authorize` | Admin | Autorizar/recusar troca |
| `PUT` | `/exchanges/confirm-receipt` | Admin | Confirmar recebimento |
| `PUT` | `/exchanges/status` | Admin | Atualizar status diretamente |

---

## ✅ Validações Importantes

1. **Pedido deve estar ENTREGUE** para solicitar troca
2. **Quantidade não pode exceder** quantidade comprada
3. **Cliente só pode ver** suas próprias trocas
4. **Admin pode ver** todas as trocas
5. **Não pode duplicar** produtos na mesma requisição

---

## 🎯 Casos de Uso Comuns

### Cliente quer trocar 2 produtos diferentes:
```javascript
POST /exchanges
{
  "orderId": 1,
  "items": [
    { "orderProductId": 10, "quantity": 1, "reason": "Defeito" },
    { "orderProductId": 11, "quantity": 2, "reason": "Não gostei" }
  ]
}
```

### Cliente quer devolver tudo de um produto:
```javascript
POST /exchanges
{
  "orderId": 1,
  "items": [
    { "orderProductId": 10, "quantity": 3, "reason": "Devolução total" }
  ]
}
```

### Admin visualiza trocas pendentes:
```javascript
GET /exchanges/pending
// Retorna todas com status "TROCA SOLICITADA"
```

### Admin autoriza e depois confirma:
```javascript
// 1. Autorizar
PUT /exchanges/authorize
{ "exchangeId": 1, "action": "APPROVE" }

// 2. Confirmar recebimento (gera cupom)
PUT /exchanges/confirm-receipt
{ "exchangeId": 1, "returnToStock": true }
```

---

## 🔄 Fluxo de Status

```
TROCA SOLICITADA
    ↓ (Admin autoriza)
TROCA ACEITA
    ↓ (Admin confirma recebimento)
TROCA CONCLUÍDA (cupom gerado)

TROCA SOLICITADA
    ↓ (Admin recusa)
TROCA RECUSADA (final)
```

---

## 💡 Dicas para Frontend

1. **Sempre valide** quantidade antes de enviar
2. **Mostre feedback** visual do status da troca
3. **Notifique** quando cupom for gerado
4. **Permita cancelar** seleção antes de enviar
5. **Valide** que pedido está ENTREGUE antes de mostrar botão


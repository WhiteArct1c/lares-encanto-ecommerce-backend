# Prompt para Implementação Frontend - Módulo de Vendas

## Contexto

O backend de um e-commerce em Java Spring Boot foi atualizado com validações e funcionalidades críticas para a finalização de compras. Este prompt descreve as mudanças e como o frontend deve se comportar.

---

## Mudanças no Backend

### 1. Validações Adicionadas na Criação de Pedido

O endpoint `POST /orders` agora valida:

#### a) Estoque Disponível

- **Validação:** Verifica se a quantidade solicitada está disponível
- **Erro retornado:** HTTP 400 com mensagem: `"Quantidade solicitada (X) para o produto Y é maior que a disponível (Z)"`
- **Ação do frontend:**
  - Exibir mensagem de erro clara ao usuário
  - Atualizar carrinho removendo ou ajustando quantidade do produto
  - Permitir que usuário ajuste quantidade ou remova item

#### b) Validação de Pagamentos

- **Validação 1:** Soma dos pagamentos deve ser igual ao totalPrice
- **Erro retornado:** HTTP 400: `"A soma dos pagamentos (R$ X) não confere com o valor total do pedido (R$ Y)"`
- **Ação do frontend:**

  - Validar soma antes de enviar requisição
  - Exibir erro se houver divergência
  - Recalcular valores automaticamente

- **Validação 2:** Valor mínimo de R$ 10,00 por cartão de crédito
- **Erro retornado:** HTTP 400: `"O valor mínimo por cartão de crédito é R$ 10,00. Valor informado: R$ X"`
- **Ação do frontend:**
  - Validar valor mínimo antes de permitir adicionar cartão
  - Exibir aviso se valor for menor que R$ 10,00
  - Bloquear finalização se não atender

#### c) Validações Gerais

- **Erro:** `"É necessário informar pelo menos uma forma de pagamento"`
- **Erro:** `"É necessário informar pelo menos um produto no pedido"`
- **Ação do frontend:** Validar antes de enviar requisição

---

### 2. Novo Endpoint: Atualização de Status

**Endpoint:** `PUT /orders/status`

**Request Body:**

```json
{
  "orderId": 1,
  "statusName": "APROVADO"
}
```

**Status disponíveis:**

- `EM PROCESSAMENTO` (inicial)
- `APROVADO`
- `REPROVADO`
- `CANCELADO`
- `EM TRANSPORTE`
- `ENTREGUE`
- `TROCA SOLICITADA`
- `TROCA ACEITA`
- `TROCA CONCLUÍDA`
- `TROCA RECUSADA`
- `DEVOLUÇÃO SOLICITADA`
- `DEVOLUÇÃO RECUSADA`
- `DEVOLUÇÃO CONCLUÍDA`

**Validações de transição:**

- `EM PROCESSAMENTO` → `APROVADO`, `REPROVADO`, `CANCELADO`
- `APROVADO` → `EM TRANSPORTE`, `CANCELADO`
- `EM TRANSPORTE` → `ENTREGUE`
- `ENTREGUE` → Status com "TROCA" ou "DEVOLUÇÃO"

**Erro retornado:** HTTP 400: `"Não é possível alterar status de X para Y"`

---

## Tarefas para o Frontend

### 1. Tela de Finalização de Compra

#### Validações no Frontend (antes de enviar)

- [ ] Validar que soma dos pagamentos = totalPrice
- [ ] Validar que cada cartão tem valor mínimo de R$ 10,00
- [ ] Validar que há pelo menos 1 produto
- [ ] Validar que há pelo menos 1 forma de pagamento
- [ ] Exibir mensagens de erro claras e específicas

#### Tratamento de Erros do Backend

- [ ] Capturar erro 400 e exibir mensagem ao usuário
- [ ] Se erro for de estoque:
  - [ ] Atualizar carrinho removendo produtos sem estoque
  - [ ] Ajustar quantidades automaticamente se possível
  - [ ] Exibir lista de produtos afetados
  - [ ] Permitir que usuário ajuste manualmente
- [ ] Se erro for de pagamento:
  - [ ] Destacar campos com problema
  - [ ] Exibir valores corretos esperados
  - [ ] Permitir correção

#### Feedback Visual

- [ ] Loading durante criação do pedido
- [ ] Mensagem de sucesso ao criar pedido
- [ ] Exibir número do pedido criado
- [ ] Redirecionar para página de confirmação

---

### 2. Tela de Detalhes do Pedido (Cliente)

#### Exibir Status Atual

- [ ] Mostrar status atual do pedido
- [ ] Badge/indicador visual do status
- [ ] Histórico de mudanças de status (se disponível)

#### Ações Disponíveis

- [ ] Se status = `EM PROCESSAMENTO`: Mostrar "Aguardando aprovação"
- [ ] Se status = `APROVADO`: Mostrar "Pedido aprovado, aguardando envio"
- [ ] Se status = `EM TRANSPORTE`: Mostrar "Pedido em trânsito"
- [ ] Se status = `ENTREGUE`: Mostrar "Pedido entregue" + opção de troca/devolução
- [ ] Se status = `REPROVADO`: Mostrar "Pedido reprovado" + motivo (se disponível)
- [ ] Se status = `CANCELADO`: Mostrar "Pedido cancelado"

---

### 3. Tela de Gestão de Pedidos (Administrador)

#### Lista de Pedidos

- [ ] Filtrar por status
- [ ] Exibir pedidos pendentes (`EM PROCESSAMENTO`)
- [ ] Badge visual para cada status

#### Ações de Administrador

- [ ] Botão "Aprovar" para pedidos `EM PROCESSAMENTO`

  - [ ] Ao clicar, chamar `PUT /orders/status` com `statusName: "APROVADO"`
  - [ ] Confirmar ação antes de executar
  - [ ] Atualizar lista após aprovação
  - [ ] Exibir mensagem de sucesso

- [ ] Botão "Reprovar" para pedidos `EM PROCESSAMENTO`

  - [ ] Solicitar motivo da reprovação (campo de texto)
  - [ ] Chamar `PUT /orders/status` com `statusName: "REPROVADO"`
  - [ ] Confirmar ação
  - [ ] Atualizar lista

- [ ] Botão "Despachar" para pedidos `APROVADO`

  - [ ] Chamar `PUT /orders/status` com `statusName: "EM TRANSPORTE"`
  - [ ] Confirmar ação
  - [ ] Atualizar lista

- [ ] Botão "Confirmar Entrega" para pedidos `EM TRANSPORTE`
  - [ ] Chamar `PUT /orders/status` com `statusName: "ENTREGUE"`
  - [ ] Confirmar ação
  - [ ] Atualizar lista

#### Validações Visuais

- [ ] Desabilitar botões para transições inválidas
- [ ] Exibir tooltip explicando por que ação não está disponível
- [ ] Exibir erro se tentar transição inválida

---

### 4. Componente de Seleção de Pagamento

#### Múltiplos Cartões

- [ ] Permitir adicionar até 2 cartões
- [ ] Calcular valor de cada cartão automaticamente
- [ ] Validar que soma = totalPrice
- [ ] Validar valor mínimo R$ 10,00 por cartão

#### Feedback Visual

- [ ] Exibir valor calculado para cada cartão
- [ ] Exibir total da soma
- [ ] Destacar se houver divergência
- [ ] Exibir aviso se cartão < R$ 10,00

---

### 5. Componente de Carrinho

#### Validação de Estoque

- [ ] Ao adicionar produto, verificar estoque disponível
- [ ] Ao finalizar compra, validar estoque novamente
- [ ] Se estoque mudou, atualizar carrinho automaticamente
- [ ] Exibir aviso: "Estoque atualizado, alguns itens foram ajustados"

#### Tratamento de Erros

- [ ] Se produto ficou sem estoque: Remover do carrinho
- [ ] Se quantidade disponível < solicitada: Ajustar para disponível
- [ ] Exibir lista de produtos afetados
- [ ] Permitir que usuário confirme ajustes

---

## Exemplos de Requisições

### Criar Pedido

```javascript
POST /orders
Headers: {
  "Authorization": "Bearer {token}",
  "Content-Type": "application/json"
}
Body: {
  "address": {
    "id": "1",
    "title": "Casa",
    "cep": "12345-678",
    // ... outros campos
  },
  "orderPayments": [
    {
      "id": null,
      "installments": 1,
      "installmentValue": 100.00,
      "paymentMethod": "CREDIT_CARD",
      "creditCard": {
        "id": 1,
        "cardNumber": "1234",
        // ... outros campos
      }
    }
  ],
  "orderProducts": [
    {
      "id": null,
      "quantity": 2,
      "product": {
        "id": 1,
        // ... outros campos
      }
    }
  ],
  "shipping": {
    "id": null,
    "name": "Sedex",
    "deliveryTime": "5 dias",
    "price": 15.00
  },
  "type": "COMPRA",
  "totalPrice": 215.00
}
```

### Atualizar Status (Admin)

```javascript
PUT /orders/status
Headers: {
  "Authorization": "Bearer {token}",
  "Content-Type": "application/json"
}
Body: {
  "orderId": 1,
  "statusName": "APROVADO"
}
```

---

## Tratamento de Erros

### Estrutura de Erro Retornada

```json
{
  "status": "400",
  "message": "Mensagem de erro descritiva",
  "data": null
}
```

### Exemplos de Mensagens

- `"Quantidade solicitada (5) para o produto Mesa é maior que a disponível (3)"`
- `"O valor mínimo por cartão de crédito é R$ 10,00. Valor informado: R$ 5,00"`
- `"A soma dos pagamentos (R$ 150,00) não confere com o valor total do pedido (R$ 200,00)"`
- `"Não é possível alterar status de EM PROCESSAMENTO para ENTREGUE"`

### Ações do Frontend

- [ ] Capturar erro 400
- [ ] Exibir mensagem em toast/alert
- [ ] Destacar campos com problema
- [ ] Permitir correção
- [ ] Não fechar modal/formulário automaticamente

---

## Checklist de Implementação

### Validações no Frontend

- [ ] Validar soma de pagamentos = totalPrice
- [ ] Validar valor mínimo R$ 10,00 por cartão
- [ ] Validar pelo menos 1 produto
- [ ] Validar pelo menos 1 pagamento
- [ ] Validar estoque antes de enviar

### Tratamento de Erros

- [ ] Capturar erro 400
- [ ] Exibir mensagem clara
- [ ] Ajustar carrinho se estoque mudou
- [ ] Destacar campos com problema

### Interface de Admin

- [ ] Lista de pedidos com filtros
- [ ] Botões de ação por status
- [ ] Validação de transições
- [ ] Confirmação de ações
- [ ] Feedback visual

### Interface de Cliente

- [ ] Exibir status do pedido
- [ ] Histórico de status
- [ ] Ações disponíveis por status
- [ ] Feedback de mudanças

---

## Observações Importantes

1. **Validações Duplas:** O frontend deve validar antes de enviar, mas também tratar erros do backend (estoque pode mudar entre validação e envio)

2. **Estoque Dinâmico:** Estoque pode mudar entre adicionar ao carrinho e finalizar compra. Frontend deve estar preparado para ajustar automaticamente.

3. **Múltiplos Cartões:** Suporta até 2 cartões, cada um com valor mínimo de R$ 10,00, exceto quando há cupons (não implementado ainda).

4. **Status Automático:** Ao aprovar/reprovar, o estoque é gerenciado automaticamente pelo backend. Frontend apenas precisa atualizar a interface.

5. **Transições de Status:** Algumas transições são inválidas. Frontend deve desabilitar ações inválidas e explicar o motivo.

---

## Prioridades

### Alta Prioridade

1. Validações de pagamento no frontend
2. Tratamento de erros de estoque
3. Interface de aprovação/reprovação (admin)

### Média Prioridade

4. Ajuste automático de carrinho
5. Histórico de status
6. Feedback visual de status

### Baixa Prioridade

7. Animações de transição
8. Notificações em tempo real
9. Gráficos de status

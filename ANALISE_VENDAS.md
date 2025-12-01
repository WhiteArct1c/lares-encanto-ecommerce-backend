# Análise Detalhada - Módulo de Vendas (Momento da Finalização da Compra)

## Resumo Executivo

**Porcentagem de Conclusão do Módulo de Vendas: ~45%**

Focando apenas no **momento da finalização da compra** (backend), excluindo o carrinho que é gerenciado pelo frontend.

---

## 1. REQUISITOS FUNCIONAIS - MOMENTO DA VENDA

### RF0033 – Realizar compra
**Status: ⚠️ PARCIALMENTE IMPLEMENTADO**

**O que está implementado:**
- ✅ Endpoint POST `/orders` existe
- ✅ Criação de pedido com produtos, pagamentos e endereço
- ✅ Status inicial: "EM PROCESSAMENTO" (RF0037)

**O que falta:**
- ❌ Validação completa de estoque antes de criar o pedido
- ❌ Validação de quantidade disponível vs quantidade solicitada
- ❌ Validação de produtos ativos
- ❌ Notificação se estoque mudou entre adição ao carrinho e finalização

**Código relevante:**
```228:242:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
private OrderProduct buildOrderProduct(Order order, OrderProductResponseDTO productDTO) {
    Product product = productRepository.findAvailableProductById(Long.valueOf(productDTO.product().id()))
            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    Stock stock = stockRepository.findByProductId(product.getId())
            .orElseThrow(() -> new EntityNotFoundException("Estoque deste produto não encontrado"));

    OrderProduct orderProduct = new OrderProduct();
    orderProduct.setQuantity(productDTO.quantity());
    orderProduct.setProduct(product);
    orderProduct.setOrder(order);

    stock.setReservedQuantity(productDTO.quantity()); //bloqueia os produtos

    return orderProduct;
}
```

**Problemas identificados:**
1. Não valida se `quantity` solicitada <= `stock.quantity - stock.reservedQuantity` (disponível)
2. Não valida se produto está ativo (apenas verifica no `findAvailableProductById`)
3. Não notifica se estoque mudou
4. Não atualiza quantidade automaticamente se insuficiente
5. Não remove item se sem estoque

---

### RF0034 – Calcular frete
**Status: ⚠️ PARCIALMENTE IMPLEMENTADO**

**O que está implementado:**
- ✅ Estrutura `OrderShipment` existe
- ✅ Campos: name, deliveryTime, price

**O que falta:**
- ❌ Cálculo automático baseado em itens e endereço
- ⚠️ O cálculo parece ser feito no frontend e enviado no request

**Código relevante:**
```244:252:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
private OrderShipment buildOrderShipment(Order order, OrderShipmentResponseDTO shipmentDTO) {
    OrderShipment shipment = new OrderShipment();
    shipment.setName(shipmentDTO.name());
    shipment.setDeliveryTime(shipmentDTO.deliveryTime());
    shipment.setPrice(shipmentDTO.price());
    shipment.setOrder(order);

    return orderShipmentRepository.save(shipment);
}
```

---

### RF0035 – Selecionar endereço
**Status: ✅ IMPLEMENTADO**

**O que está implementado:**
- ✅ Endereço pode ser selecionado (por ID) ou novo
- ✅ Novo endereço é salvo e associado ao cliente
- ✅ Endereço é associado ao pedido

**Código relevante:**
```149:178:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
private Address buildAddress(Customer customer, Order order, AddressRequestDTO addressDTO) {
    if(addressDTO.id() == null || addressDTO.id().isEmpty()){
        Address newAddress = new Address();
        // ... criação de novo endereço
        Address savedAddress = addressRepository.save(newAddress);
        order.setAddress(savedAddress);
        return savedAddress;
    }else{
        Address address = addressRepository.findById(Long.valueOf(addressDTO.id()))
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));
        order.setAddress(address);
        return address;
    }
}
```

---

### RF0036 – Selecionar pagamento
**Status: ⚠️ PARCIALMENTE IMPLEMENTADO**

**O que está implementado:**
- ✅ Múltiplos cartões suportados (Set<OrderPayment>)
- ✅ Cartão pode ser do perfil ou novo
- ✅ Novo cartão é salvo e associado ao cliente

**O que falta:**
- ❌ Sistema de cupons (promocional e troca)
- ❌ Validação de valor mínimo por cartão (R$ 10,00)
- ❌ Lógica de pagamento misto (cupons + cartão)
- ❌ Validação de operadora de cartão

**Código relevante:**
```187:220:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
private Set<OrderPayment> buildOrderPayments(Customer customer, Order order, List<OrderPaymentResponseDTO> paymentDTOs) {
    return paymentDTOs.stream()
            .map(paymentDTO -> buildOrderPayment(customer, order, paymentDTO))
            .collect(Collectors.toSet());
}

private OrderPayment buildOrderPayment(Customer customer, Order order, OrderPaymentResponseDTO paymentDTO) {
    // ... busca ou cria cartão
    OrderPayment payment = new OrderPayment();
    payment.setInstallments(paymentDTO.installments());
    payment.setInstallmentValue(paymentDTO.installmentValue());
    payment.setMethod(OrderPayment.PaymentMethod.valueOf(paymentDTO.paymentMethod()));
    payment.setCreditCard(orderCreditCard);
    payment.setOrder(order);
    return orderPaymentRepository.save(payment);
}
```

---

### RF0037 – Finalizar Compra
**Status: ✅ IMPLEMENTADO**

**O que está implementado:**
- ✅ Status inicial: "EM PROCESSAMENTO"
- ✅ Pedido é criado e persistido

**Código relevante:**
```254:257:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
private OrderStatus getDefaultOrderStatus() {
    return orderStatusRepository.findByName("EM PROCESSAMENTO")
            .orElseThrow(() -> new EntityNotFoundException("Status padrão 'EM PROCESSAMENTO' não encontrado"));
}
```

---

### RF0038 – Despachar produtos
**Status: ❌ NÃO IMPLEMENTADO**

**O que falta:**
- ❌ Endpoint para mudar status para "EM TRÂNSITO"
- ❌ Validação de que pedido está "APROVADO"
- ❌ Lógica de autorização de administrador

---

### RF0039 – Produtos entregues
**Status: ❌ NÃO IMPLEMENTADO**

**O que falta:**
- ❌ Endpoint para mudar status para "ENTREGUE"
- ❌ Validação de que pedido está "EM TRÂNSITO"
- ❌ Lógica de autorização de administrador

---

## 2. REGRAS DE NEGÓCIO - MOMENTO DA VENDA

### RN0031 – Estoque na adição
**Status: ❌ NÃO IMPLEMENTADO CORRETAMENTE**

**Requisito:** Não permitir adicionar item sem estoque ou quantidade superior à disponível.

**Problema atual:**
- ❌ Não valida se `quantity` <= `stock.quantity - stock.reservedQuantity`
- ❌ Apenas verifica se produto existe e está ativo
- ❌ Não verifica estoque disponível real

**Código atual:**
```228:242:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
// Não valida quantidade disponível antes de reservar
stock.setReservedQuantity(productDTO.quantity()); //bloqueia os produtos
```

**O que deveria fazer:**
```java
int availableQuantity = stock.getQuantity() - stock.getReservedQuantity();
if (productDTO.quantity() > availableQuantity) {
    throw new BusinessException("Quantidade solicitada maior que disponível");
}
if (availableQuantity <= 0) {
    throw new BusinessException("Produto sem estoque disponível");
}
stock.setReservedQuantity(stock.getReservedQuantity() + productDTO.quantity());
```

---

### RN0032 – Validação na compra
**Status: ❌ NÃO IMPLEMENTADO**

**Requisito:** Se estoque mudar entre adição e finalização: notificar, atualizar quantidade ou remover item.

**O que falta:**
- ❌ Não há verificação de mudança de estoque
- ❌ Não há notificação ao cliente
- ❌ Não há atualização automática de quantidade
- ❌ Não há remoção de itens sem estoque

---

### RN0033 – Cupom promocional
**Status: ❌ NÃO IMPLEMENTADO**

**Requisito:** Apenas um cupom promocional por compra.

**O que falta:**
- ❌ Não há entidade `Coupon` ou `PromotionalCoupon`
- ❌ Não há validação de cupom
- ❌ Não há aplicação de desconto

---

### RN0034 – Múltiplos cartões
**Status: ⚠️ PARCIALMENTE IMPLEMENTADO**

**Requisito:** Permitido mais de um cartão. Valor mínimo por cartão: R$ 10,00.

**O que está implementado:**
- ✅ Múltiplos cartões suportados (Set<OrderPayment>)

**O que falta:**
- ❌ **Validação de valor mínimo R$ 10,00 por cartão**
- ❌ Validação de soma dos valores dos cartões = totalPrice

**Código atual:**
```187:220:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
// Não valida valor mínimo por cartão
// Não valida soma dos pagamentos
```

**O que deveria fazer:**
```java
private void validatePayments(Set<OrderPaymentResponseDTO> payments, Double totalPrice) {
    BigDecimal totalPayments = payments.stream()
        .map(p -> p.installmentValue().multiply(BigDecimal.valueOf(p.installments())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // Validar soma
    if (totalPayments.compareTo(BigDecimal.valueOf(totalPrice)) != 0) {
        throw new BusinessException("Soma dos pagamentos não confere com total");
    }
    
    // Validar mínimo por cartão
    for (OrderPaymentResponseDTO payment : payments) {
        BigDecimal paymentTotal = payment.installmentValue()
            .multiply(BigDecimal.valueOf(payment.installments()));
        if (paymentTotal.compareTo(new BigDecimal("10.00")) < 0) {
            throw new BusinessException("Valor mínimo por cartão é R$ 10,00");
        }
    }
}
```

---

### RN0035 – Cupons + Cartão
**Status: ❌ NÃO IMPLEMENTADO**

**Requisito:** Considerar sempre o valor máximo dos cupons primeiro. Permite-se cartão < R$ 10,00 neste caso específico.

**O que falta:**
- ❌ Sistema de cupons
- ❌ Lógica de aplicação de cupons
- ❌ Exceção para valor mínimo quando há cupons

---

### RN0036 – Troco em Cupom
**Status: ❌ NÃO IMPLEMENTADO**

**Requisito:** Se valor dos cupons superar a compra, gerar cupom de troca com a diferença. Não permitir uso desnecessário de múltiplos cupons.

**O que falta:**
- ❌ Sistema de cupons
- ❌ Geração de cupom de troca
- ❌ Validação de uso otimizado de cupons

---

### RN0037 – Validação final
**Status: ⚠️ PARCIALMENTE IMPLEMENTADO**

**Requisito:** Validar cupons e aceite da operadora.

**O que está implementado:**
- ✅ Estrutura básica de validação

**O que falta:**
- ❌ Validação de cupons
- ❌ **Validação de aceite da operadora de cartão** (integração com gateway)
- ❌ Validação de estoque (já mencionado)

---

### RN0038 – Status de Aprovação
**Status: ⚠️ PARCIALMENTE IMPLEMENTADO**

**Requisito:** Sucesso = APROVADA. Falha = REPROVADA.

**O que está implementado:**
- ✅ Status "APROVADO" e "REPROVADO" existem no banco

**O que falta:**
- ❌ Lógica automática de aprovação/reprovação
- ❌ Integração com gateway de pagamento
- ❌ Endpoint para atualizar status manualmente
- ❌ Validação de pagamento antes de aprovar

**Status disponíveis no banco:**
```1:13:src/main/resources/db/migration/V6__insert-order-statuses-values.sql
INSERT INTO order_status (name, created_at, updated_at) VALUES
    ('EM PROCESSAMENTO', NOW(), NOW()),
    ('REPROVADO', NOW(), NOW()),
    ('APROVADO', NOW(), NOW()),
    ('CANCELADO', NOW(), NOW()),
    ('EM TRANSPORTE', NOW(), NOW()),
    ('TROCA SOLICITADA', NOW(), NOW()),
    ('TROCA ACEITA', NOW(), NOW()),
    ('TROCA CONCLUÍDA', NOW(), NOW()),
    ('TROCA RECUSADA', NOW(), NOW()),
    ('DEVOLUÇÃO SOLICITADA', NOW(), NOW()),
    ('DEVOLUÇÃO RECUSADA', NOW(), NOW()),
    ('DEVOLUÇÃO CONCLUÍDA', NOW(), NOW());
```

---

### RN0028 – Baixa efetiva
**Status: ❌ NÃO IMPLEMENTADO**

**Requisito:** Baixa no estoque apenas para compras efetivadas (não mais EM PROCESSAMENTO). Itens de compras reprovadas são desbloqueados.

**O que está implementado:**
- ✅ Reserva de estoque (`reservedQuantity`) é feita na criação

**O que falta:**
- ❌ **Baixa efetiva** quando status muda para "APROVADO"
- ❌ **Desbloqueio** quando status muda para "REPROVADO" ou "CANCELADO"
- ❌ Lógica de atualização de estoque baseada em status

**Código atual:**
```239:239:src/main/java/com/laresencanto/laresencantorestapi/service/OrderService.java
stock.setReservedQuantity(productDTO.quantity()); //bloqueia os produtos
```

**O que deveria fazer:**
```java
// Quando status muda para APROVADO:
stock.setQuantity(stock.getQuantity() - orderProduct.getQuantity());
stock.setReservedQuantity(stock.getReservedQuantity() - orderProduct.getQuantity());

// Quando status muda para REPROVADO/CANCELADO:
stock.setReservedQuantity(stock.getReservedQuantity() - orderProduct.getQuantity());
```

---

## 3. RESUMO DE PROBLEMAS CRÍTICOS

### 🔴 CRÍTICOS (Bloqueiam funcionamento correto)

1. **Validação de Estoque (RN0031)**
   - Não valida quantidade disponível antes de reservar
   - Pode reservar mais do que tem disponível

2. **Validação de Valor Mínimo por Cartão (RN0034)**
   - Não valida R$ 10,00 mínimo por cartão
   - Permite valores menores

3. **Baixa Efetiva de Estoque (RN0028)**
   - Estoque é apenas reservado, nunca baixado
   - Não há desbloqueio em caso de reprovação

4. **Validação de Soma de Pagamentos**
   - Não valida se soma dos pagamentos = totalPrice
   - Pode criar pedidos com valores incorretos

### 🟡 IMPORTANTES (Funcionalidade incompleta)

5. **Validação na Compra (RN0032)**
   - Não verifica mudança de estoque
   - Não notifica/atualiza automaticamente

6. **Status de Aprovação (RN0038)**
   - Não há lógica automática de aprovação/reprovação
   - Não há integração com gateway

7. **Validação de Operadora (RN0037)**
   - Não valida aceite da operadora

### 🟢 FALTANTES (Funcionalidades não implementadas)

8. **Sistema de Cupons**
   - Não existe entidade/service
   - RN0033, RN0035, RN0036 não podem ser implementadas

9. **Despacho e Entrega (RF0038, RF0039)**
   - Não há endpoints para mudar status

---

## 4. RECOMENDAÇÕES PRIORITÁRIAS

### Prioridade ALTA (Corrigir imediatamente)

1. **Implementar validação de estoque disponível**
   ```java
   int available = stock.getQuantity() - stock.getReservedQuantity();
   if (quantity > available) throw new BusinessException(...);
   ```

2. **Implementar validação de valor mínimo por cartão**
   ```java
   if (paymentTotal < 10.00) throw new BusinessException(...);
   ```

3. **Implementar baixa efetiva de estoque**
   - Criar método que atualiza estoque quando status muda para APROVADO
   - Criar método que desbloqueia quando status muda para REPROVADO/CANCELADO

4. **Validar soma de pagamentos**
   ```java
   if (sumPayments != totalPrice) throw new BusinessException(...);
   ```

### Prioridade MÉDIA

5. Implementar endpoints de atualização de status (APROVADO, REPROVADO, EM TRÂNSITO, ENTREGUE)
6. Implementar validação de mudança de estoque (RN0032)
7. Implementar integração com gateway de pagamento (ou mock)

### Prioridade BAIXA

8. Implementar sistema de cupons completo
9. Implementar notificações

---

## 5. CONCLUSÃO

**Status Atual: ~45% dos requisitos de venda implementados**

**Funcionalidades que funcionam:**
- ✅ Criação básica de pedido
- ✅ Múltiplos cartões (estrutura)
- ✅ Seleção/criação de endereço
- ✅ Status inicial correto

**Funcionalidades que NÃO funcionam corretamente:**
- ❌ Validação de estoque
- ❌ Validação de pagamentos
- ❌ Baixa de estoque
- ❌ Sistema de cupons
- ❌ Aprovação/reprovação automática

**Ação necessária:** Corrigir os 4 problemas críticos antes de considerar o módulo funcional.

---

## 6. TROCAS E DEVOLUÇÕES PARCIAIS

### Status: ❌ NÃO IMPLEMENTADO

**Observação:** O usuário mencionou que trocas e devoluções podem ser parciais. A estrutura atual do banco de dados **suporta** isso, mas não há implementação.

**Estrutura atual que suporta parciais:**
- ✅ `OrderProduct` permite múltiplos produtos em um pedido
- ✅ Cada `OrderProduct` tem `quantity` individual
- ✅ Status de troca/devolução existem no banco

**O que falta para suportar trocas/devoluções parciais:**

1. **Entidade para rastrear itens trocados/devolvidos**
   - Não há como marcar quais `OrderProduct` foram trocados/devolvidos
   - Não há quantidade parcial trocada/devolvida

2. **Endpoints necessários:**
   - `POST /orders/{id}/exchange` - Solicitar troca de itens específicos
   - `POST /orders/{id}/return` - Solicitar devolução de itens específicos
   - `PUT /orders/{id}/exchange/authorize` - Autorizar troca (admin)
   - `PUT /orders/{id}/return/confirm` - Confirmar recebimento (admin)

3. **Lógica de negócio:**
   - Validar que pedido está "ENTREGUE" (RN0043)
   - Permitir selecionar quais produtos e quantidades trocar/devolver
   - Gerar cupom de troca apenas para itens confirmados
   - Reentrada de estoque apenas para itens confirmados

**Recomendação de estrutura para suportar parciais:**

```java
@Entity
public class OrderExchange {
    @Id
    private Long id;
    
    @ManyToOne
    private Order order;
    
    @ManyToOne
    private OrderProduct orderProduct; // Item específico
    
    @Column
    private Integer quantity; // Quantidade parcial trocada
    
    @ManyToOne
    private OrderStatus status; // TROCA SOLICITADA, TROCA ACEITA, etc.
    
    @Column
    private String reason;
    
    // ... campos de auditoria
}
```

**Status atual:** Apenas estrutura de status existe, mas não há entidades/services/controllers para implementar a funcionalidade.


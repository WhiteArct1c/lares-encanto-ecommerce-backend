package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.order.OrderCreateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.order.OrderStatusUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.request.payment.PaymentWebhookDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderResponseDTO;
import com.laresencanto.laresencantorestapi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseDTO<OrderResponseDTO> getCustomerOrders() {
        return orderService.listCustomerOrders();
    }

    @GetMapping("/all")
    public ResponseDTO<OrderResponseDTO> getAllOrders() {
        return orderService.listAllOrders();
    }

    @GetMapping("/pending")
    public ResponseDTO<OrderResponseDTO> getPendingOrders() {
        return orderService.listPendingOrders();
    }

    @GetMapping("/canceled")
    public ResponseDTO<OrderResponseDTO> getCanceledOrders() {
        return orderService.listCanceledOrders();
    }

    @GetMapping("/finished")
    public ResponseDTO<OrderResponseDTO> getFinishedOrders() {
        return orderService.listFinishedOrders();
    }

    @PostMapping
    public ResponseDTO<OrderResponseDTO> createOrder(@RequestBody @Valid OrderCreateRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @PutMapping("/status")
    public ResponseDTO<OrderResponseDTO> updateOrderStatus(@RequestBody @Valid OrderStatusUpdateDTO statusUpdateDTO) {
        return orderService.updateOrderStatus(statusUpdateDTO);
    }

    /**
     * Simula a validação de pagamento para um pedido específico
     * 
     * @param orderId ID do pedido
     * @param approve true para aprovar, false para reprovar, null para usar lógica
     *                automática (opcional)
     * @return Pedido atualizado
     */
    @PostMapping("/{orderId}/mock-payment-validation")
    public ResponseDTO<OrderResponseDTO> mockPaymentValidation(
            @PathVariable Long orderId,
            @RequestParam(required = false) Boolean approve) {
        return orderService.mockPaymentValidation(orderId, approve);
    }

    /**
     * Processa todos os pedidos pendentes automaticamente
     * Simula validação de pagamento para todos os pedidos em processamento
     * 
     * @param approveAll true para aprovar todos, false para reprovar todos, null
     *                   para usar lógica automática (opcional)
     * @return Lista de pedidos processados
     */
    @PostMapping("/process-pending")
    public ResponseDTO<OrderResponseDTO> processAllPendingOrders(
            @RequestParam(required = false) Boolean approveAll) {
        return orderService.processAllPendingOrders(approveAll);
    }

    /**
     * Endpoint para receber webhook de pagamento (simula como um gateway real
     * enviaria)
     * 
     * Este endpoint simula como um gateway de pagamento (Stripe, Mercado Pago, etc)
     * notificaria o sistema sobre o status do pagamento via webhook.
     * 
     * Exemplo de uso:
     * POST /orders/payment-webhook
     * {
     * "orderId": 1,
     * "paymentStatus": "APPROVED", // ou "REJECTED", "PENDING"
     * "transactionId": "txn_123456",
     * "message": "Pagamento aprovado com sucesso"
     * }
     * 
     * @param webhookDTO Dados do webhook de pagamento
     * @return Pedido atualizado
     */
    @PostMapping("/payment-webhook")
    public ResponseDTO<OrderResponseDTO> processPaymentWebhook(
            @RequestBody @Valid PaymentWebhookDTO webhookDTO) {
        return orderService.processPaymentWebhook(webhookDTO);
    }
}

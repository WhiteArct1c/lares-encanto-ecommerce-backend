package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.order.OrderCreateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.order.OrderStatusUpdateDTO;
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

    @PostMapping
    public ResponseDTO<OrderResponseDTO> createOrder(@RequestBody @Valid OrderCreateRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @PutMapping("/status")
    public ResponseDTO<OrderResponseDTO> updateOrderStatus(@RequestBody @Valid OrderStatusUpdateDTO statusUpdateDTO) {
        return orderService.updateOrderStatus(statusUpdateDTO);
    }
}

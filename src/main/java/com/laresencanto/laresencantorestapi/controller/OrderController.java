package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.order.OrderCreateRequestDTO;
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

    @GetMapping("/all")
    public ResponseDTO<OrderResponseDTO> getAllOrders() {
        return orderService.listAllOrders();
    }

    @PostMapping
    public ResponseDTO<OrderResponseDTO> createOrder(@RequestBody @Valid OrderCreateRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}

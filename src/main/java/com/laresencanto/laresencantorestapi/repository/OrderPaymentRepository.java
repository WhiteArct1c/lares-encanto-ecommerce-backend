package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.order.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
}

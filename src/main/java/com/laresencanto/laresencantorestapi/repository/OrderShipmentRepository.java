package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.order.OrderShipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderShipmentRepository extends JpaRepository<OrderShipment, Long> {
}

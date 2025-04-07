package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}

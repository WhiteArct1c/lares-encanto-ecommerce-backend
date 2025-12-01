package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.order.OrderCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCouponRepository extends JpaRepository<OrderCoupon, Long> {
    
    List<OrderCoupon> findAllByOrderId(Long orderId);
}


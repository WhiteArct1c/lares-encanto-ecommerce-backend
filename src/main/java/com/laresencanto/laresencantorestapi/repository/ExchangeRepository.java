package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.exchange.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    
    List<Exchange> findAllByOrderId(Long orderId);
    
    @Query("SELECT DISTINCT e FROM Exchange e " +
           "LEFT JOIN FETCH e.order o " +
           "LEFT JOIN FETCH e.items ei " +
           "LEFT JOIN FETCH ei.orderProduct op " +
           "LEFT JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.pricingGroup " +
           "LEFT JOIN FETCH e.status " +
           "WHERE e.status.name = :statusName")
    List<Exchange> findAllByStatusName(@Param("statusName") String statusName);
    
    @Query("SELECT DISTINCT e FROM Exchange e " +
           "LEFT JOIN FETCH e.order o " +
           "LEFT JOIN FETCH e.items ei " +
           "LEFT JOIN FETCH ei.orderProduct op " +
           "LEFT JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.pricingGroup " +
           "LEFT JOIN FETCH e.status " +
           "WHERE o.customer.id = :customerId")
    List<Exchange> findAllByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT DISTINCT e FROM Exchange e " +
           "LEFT JOIN FETCH e.order o " +
           "LEFT JOIN FETCH o.customer " +
           "LEFT JOIN FETCH e.items ei " +
           "LEFT JOIN FETCH ei.orderProduct op " +
           "LEFT JOIN FETCH op.product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.pricingGroup " +
           "LEFT JOIN FETCH e.status")
    List<Exchange> findAllWithRelations();
}


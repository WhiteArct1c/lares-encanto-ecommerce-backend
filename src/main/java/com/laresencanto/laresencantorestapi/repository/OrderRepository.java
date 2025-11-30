package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderProducts op " +
            "LEFT JOIN FETCH op.product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.pricingGroup " +
            "LEFT JOIN FETCH o.orderPayments " +
            "LEFT JOIN FETCH o.orderShipment " +
            "WHERE o.customer.id = :customerId")
    List<Order> findAllByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderProducts op " +
            "LEFT JOIN FETCH op.product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.pricingGroup " +
            "LEFT JOIN FETCH o.orderPayments " +
            "LEFT JOIN FETCH o.orderShipment " +
            "WHERE o.status.name = :statusName")
    List<Order> findAllByStatusName(@Param("statusName") String statusName);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderProducts op " +
            "LEFT JOIN FETCH op.product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.pricingGroup " +
            "LEFT JOIN FETCH o.orderPayments " +
            "LEFT JOIN FETCH o.orderShipment " +
            "WHERE o.status.name IN :statusNames")
    List<Order> findAllByStatusNames(@Param("statusNames") List<String> statusNames);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderProducts op " +
            "LEFT JOIN FETCH op.product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.pricingGroup " +
            "LEFT JOIN FETCH o.orderPayments " +
            "LEFT JOIN FETCH o.orderShipment")
    List<Order> findAllWithRelations();
}

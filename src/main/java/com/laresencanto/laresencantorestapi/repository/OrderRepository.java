package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.status.name IN :statusNames " +
            "AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    Double sumTotalPriceByStatusNamesAndCreatedAtBetween(@Param("statusNames") List<String> statusNames,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.status.name IN :statusNames " +
            "AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    Long countByStatusNamesAndCreatedAtBetween(@Param("statusNames") List<String> statusNames,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query(value = """
            SELECT
              EXTRACT(YEAR FROM o.created_at) AS year,
              EXTRACT(MONTH FROM o.created_at) AS month,
              c.id AS category_id,
              c.name AS category_name,
              SUM(op.unit_price * op.quantity) AS total_sales_amount,
              COUNT(DISTINCT o.id) AS total_orders
            FROM orders o
            JOIN order_products op ON op.order_id = o.id
            JOIN products p ON op.product_id = p.id
            JOIN product_categories c ON p.category_id = c.id
            JOIN order_status s ON o.status_id = s.id
            WHERE o.created_at >= :startDate
              AND o.created_at < :endDate
              AND s.name IN (:statusNames)
              AND (:categoryIdsIsNull = true OR c.id IN (:categoryIds))
            GROUP BY year, month, c.id, c.name
            ORDER BY year, month, c.name
            """, nativeQuery = true)
    List<Object[]> findSalesByCategoryAndPeriod(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                @Param("statusNames") List<String> statusNames,
                                                @Param("categoryIds") List<Integer> categoryIds,
                                                @Param("categoryIdsIsNull") boolean categoryIdsIsNull);
}

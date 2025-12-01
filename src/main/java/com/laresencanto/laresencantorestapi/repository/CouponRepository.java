package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findAllByCustomerId(Long customerId);
    
    @Query("SELECT c FROM Coupon c WHERE c.customer.id = :customerId AND c.isActive = true " +
           "AND (c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)")
    List<Coupon> findActiveCouponsByCustomerId(@Param("customerId") Long customerId);
    
    List<Coupon> findAllByExchangeId(Long exchangeId);
    
    /**
     * Busca todos os cupons promocionais
     */
    List<Coupon> findAllByCouponType(String couponType);
    
    /**
     * Busca cupons promocionais ativos e válidos
     * 
     * IMPORTANTE: Cupons promocionais podem ser usados múltiplas vezes até expirar.
     * Não filtra por valor disponível, apenas por status ativo e não expirado.
     */
    @Query("SELECT c FROM Coupon c WHERE c.couponType = 'PROMOTIONAL' AND c.isActive = true " +
           "AND (c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)")
    List<Coupon> findActivePromotionalCoupons();
}


package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.coupon.PromotionalCouponCreateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.coupon.CouponResponseDTO;
import com.laresencanto.laresencantorestapi.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * Lista todos os cupons do cliente autenticado
     * GET /coupons
     */
    @GetMapping
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> listCustomerCoupons() {
        ResponseDTO<CouponResponseDTO> response = couponService.listCustomerCoupons();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista apenas os cupons ativos e válidos do cliente autenticado
     * GET /coupons/active
     */
    @GetMapping("/active")
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> listActiveCustomerCoupons() {
        ResponseDTO<CouponResponseDTO> response = couponService.listActiveCustomerCoupons();
        return ResponseEntity.ok(response);
    }

    /**
     * Cria um cupom promocional (apenas admin)
     * POST /coupons/promotional
     */
    @PostMapping("/promotional")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> createPromotionalCoupon(
            @RequestBody @Valid PromotionalCouponCreateDTO dto) {
        ResponseDTO<CouponResponseDTO> response = couponService.createPromotionalCoupon(dto);
        return ResponseEntity.status(response.code().equals("201 CREATED") ? 201 : 200).body(response);
    }

    /**
     * Lista todos os cupons do sistema (apenas admin)
     * GET /coupons/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> listAllCoupons() {
        ResponseDTO<CouponResponseDTO> response = couponService.listAllCoupons();
        return ResponseEntity.ok(response);
    }

    /**
     * Lista cupons de um cliente específico (apenas admin)
     * GET /coupons/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> listCouponsByCustomerId(
            @PathVariable Long customerId) {
        ResponseDTO<CouponResponseDTO> response = couponService.listCouponsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os cupons promocionais disponíveis (endpoint público)
     * GET /coupons/promotional
     * Não requer autenticação - retorna apenas cupons ativos, não expirados e com valor disponível
     */
    @GetMapping("/promotional")
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> listPromotionalCoupons() {
        ResponseDTO<CouponResponseDTO> response = couponService.listPromotionalCoupons();
        return ResponseEntity.ok(response);
    }

    /**
     * Valida um cupom promocional pelo código (endpoint público)
     * GET /coupons/validate/{couponCode}
     * Não requer autenticação - usado pelo frontend para validar antes de usar
     */
    @GetMapping("/validate/{couponCode}")
    public ResponseEntity<ResponseDTO<CouponResponseDTO>> validatePromotionalCoupon(
            @PathVariable String couponCode) {
        ResponseDTO<CouponResponseDTO> response = couponService.validatePromotionalCoupon(couponCode);
        return ResponseEntity.ok(response);
    }
}


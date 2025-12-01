package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.coupon.Coupon;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.coupon.PromotionalCouponCreateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.coupon.CouponResponseDTO;
import com.laresencanto.laresencantorestapi.exception.BusinessException;
import com.laresencanto.laresencantorestapi.exception.EntityNotFoundException;
import com.laresencanto.laresencantorestapi.repository.CouponRepository;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;

    public CouponService(CouponRepository couponRepository, CustomerRepository customerRepository) {
        this.couponRepository = couponRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Lista todos os cupons do cliente autenticado
     */
    @Transactional(readOnly = true)
    public ResponseDTO<CouponResponseDTO> listCustomerCoupons() {
        Customer customer = getAuthenticatedCustomer();
        List<Coupon> coupons = couponRepository.findAllByCustomerId(customer.getId());

        List<CouponResponseDTO> couponsDTO = coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cupons encontrados com sucesso",
                couponsDTO);
    }

    /**
     * Lista apenas os cupons ativos e válidos do cliente autenticado
     */
    @Transactional(readOnly = true)
    public ResponseDTO<CouponResponseDTO> listActiveCustomerCoupons() {
        Customer customer = getAuthenticatedCustomer();
        List<Coupon> coupons = couponRepository.findActiveCouponsByCustomerId(customer.getId());

        List<CouponResponseDTO> couponsDTO = coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cupons ativos encontrados com sucesso",
                couponsDTO);
    }

    /**
     * Valida um cupom pelo código
     * Verifica se está ativo, não expirado e tem valor disponível
     * Para cupons promocionais sem cliente, não valida propriedade
     * Método público para uso em outros serviços (ex: OrderService)
     */
    @Transactional(readOnly = true)
    public Coupon validateCoupon(String couponCode, Long customerId) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new EntityNotFoundException("Cupom não encontrado"));

        // Valida propriedade do cupom
        // Cupons promocionais sem cliente (customer = null) são válidos para qualquer cliente
        // Cupons de troca e promocionais com cliente devem pertencer ao cliente
        if (coupon.getCustomer() != null && !coupon.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("Este cupom não pertence ao cliente autenticado");
        }

        // Valida se está ativo
        if (!coupon.getIsActive()) {
            throw new BusinessException("Cupom já foi utilizado e está inativo");
        }

        // Valida se não expirou
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessException("Cupom expirado");
        }

        // Valida regras específicas por tipo de cupom
        if ("EXCHANGE".equals(coupon.getCouponType())) {
            // Cupons de troca precisam ter valor disponível
            BigDecimal availableValue = coupon.getAvailableValue();
            if (availableValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Cupom não possui valor disponível");
            }
        } else if ("PROMOTIONAL".equals(coupon.getCouponType())) {
            // Cupons promocionais podem ser usados múltiplas vezes até expirar,
            // mas se houver limite de usos (maxUses), respeita esse limite
            Integer maxUses = coupon.getMaxUses();
            int usedCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
            if (maxUses != null && usedCount >= maxUses) {
                throw new BusinessException("Cupom já atingiu o limite máximo de usos");
            }
        }

        return coupon;
    }

    /**
     * Valida um cupom promocional pelo código (endpoint público)
     * Não requer autenticação - usado pelo frontend para validar antes de usar
     * 
     * @param couponCode Código do cupom
     * @return DTO com informações do cupom válido
     */
    @Transactional(readOnly = true)
    public ResponseDTO<CouponResponseDTO> validatePromotionalCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new EntityNotFoundException("Cupom não encontrado"));

        // Valida que é um cupom promocional
        if (!"PROMOTIONAL".equals(coupon.getCouponType())) {
            throw new BusinessException("Este cupom não é promocional");
        }

        // Valida se está ativo
        if (!coupon.getIsActive()) {
            throw new BusinessException("Cupom já foi utilizado e está inativo");
        }

        // Valida se não expirou
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessException("Cupom expirado");
        }

        // IMPORTANTE: Cupons promocionais podem ser usados infinitamente até expirar
        // Não valida valor disponível - cupons promocionais podem ser usados mesmo totalmente usados
        // A validação de valor disponível será feita no momento do uso (useCoupon)

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cupom promocional válido",
                List.of(convertToDTO(coupon)));
    }

    /**
     * Usa um cupom em uma compra
     * Atualiza o usedValue
     * 
     * IMPORTANTE: Cupons promocionais NÃO são desativados quando totalmente usados.
     * Eles podem ser usados múltiplas vezes até expirarem.
     * MAS em cada uso, o valor usado não pode exceder o valor original do cupom.
     * Apenas cupons de troca são desativados quando totalmente utilizados.
     */
    @Transactional
    public void useCoupon(Coupon coupon, BigDecimal amountToUse) {
        // Validação para cupons de troca: verifica valor disponível
        if ("EXCHANGE".equals(coupon.getCouponType())) {
            BigDecimal availableValue = coupon.getAvailableValue();
            if (amountToUse.compareTo(availableValue) > 0) {
                throw new BusinessException(
                        String.format("Valor solicitado (R$ %.2f) é maior que o disponível no cupom (R$ %.2f)",
                                amountToUse, availableValue));
            }
        }
        
        // Validação para cupons promocionais: valor usado não pode exceder o valor original
        // Cupons promocionais podem ser usados múltiplas vezes, mas cada uso é limitado ao valor original
        if ("PROMOTIONAL".equals(coupon.getCouponType())) {
            if (amountToUse.compareTo(coupon.getValue()) > 0) {
                throw new BusinessException(
                        String.format("Valor solicitado (R$ %.2f) excede o valor máximo do cupom promocional (R$ %.2f). " +
                                "Cupons promocionais podem ser usados múltiplas vezes, mas cada uso é limitado ao valor original do cupom.",
                                amountToUse, coupon.getValue()));
            }
        }

        // IMPORTANTE:
        // - Cupons de troca (EXCHANGE): controlados por valor (usedValue / availableValue)
        // - Cupons promocionais (PROMOTIONAL): controlados apenas por quantidade de usos (maxUses/usedCount)
        if ("EXCHANGE".equals(coupon.getCouponType())) {
            // Atualiza o valor usado apenas para cupons de troca
            BigDecimal newUsedValue = coupon.getUsedValue().add(amountToUse);
            coupon.setUsedValue(newUsedValue);

            // Desativa quando totalmente utilizado em valor
            if (newUsedValue.compareTo(coupon.getValue()) >= 0) {
                coupon.setIsActive(false);
            }
        } else if ("PROMOTIONAL".equals(coupon.getCouponType())) {
            // NÃO mexe em usedValue: o valor do cupom vale por uso, não é um saldo que esgota

            // Atualiza contagem de usos para cupons promocionais
            Integer currentUsedCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
            coupon.setUsedCount(currentUsedCount + 1);

            // Se maxUses estiver definido, desativa quando atingir o limite
            Integer maxUses = coupon.getMaxUses();
            if (maxUses != null && coupon.getUsedCount() >= maxUses) {
                coupon.setIsActive(false);
            }
        }

        couponRepository.save(coupon);
    }

    /**
     * Converte Coupon para DTO
     */
    private CouponResponseDTO convertToDTO(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getValue(),
                coupon.getUsedValue(),
                coupon.getAvailableValue(),
                coupon.getIsActive(),
                coupon.getExpiresAt(),
                coupon.getCustomer() != null ? coupon.getCustomer().getId() : null,
                coupon.getCouponType(),
                coupon.getMaxUses(),
                coupon.getUsedCount());
    }

    /**
     * Cria um cupom promocional (apenas admin)
     * Se customerId for null, o cupom é válido para qualquer cliente
     * 
     * @param dto DTO com dados do cupom promocional
     * @return Cupom criado
     */
    @Transactional
    public ResponseDTO<CouponResponseDTO> createPromotionalCoupon(PromotionalCouponCreateDTO dto) {
        // Valida que o código é único
        if (couponRepository.findByCode(dto.code()).isPresent()) {
            throw new BusinessException(String.format("Cupom com código '%s' já existe", dto.code()));
        }

        // Valida que o cliente existe (se fornecido)
        Customer customer = null;
        if (dto.customerId() != null) {
            customer = customerRepository.findById(dto.customerId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Cliente com ID %d não encontrado", dto.customerId())));
        }

        // Cria o cupom promocional
        Coupon coupon = new Coupon();
        coupon.setCode(dto.code());
        coupon.setValue(dto.value());
        coupon.setUsedValue(BigDecimal.ZERO);
        coupon.setIsActive(true);
        coupon.setExpiresAt(dto.expiresAt());
        coupon.setCustomer(customer); // Null para cupons válidos para qualquer cliente
        coupon.setExchange(null); // Cupons promocionais não estão vinculados a trocas
        coupon.setCouponType("PROMOTIONAL");
        coupon.setMaxUses(dto.maxUses());
        coupon.setUsedCount(0);

        Coupon savedCoupon = couponRepository.save(coupon);

        return new ResponseDTO<>(
                HttpStatus.CREATED.toString(),
                String.format("Cupom promocional '%s' criado com sucesso", savedCoupon.getCode()),
                List.of(convertToDTO(savedCoupon)));
    }

    /**
     * Lista todos os cupons (apenas admin)
     * 
     * @return Lista de todos os cupons do sistema
     */
    @Transactional(readOnly = true)
    public ResponseDTO<CouponResponseDTO> listAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        List<CouponResponseDTO> couponsDTO = coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cupons encontrados com sucesso",
                couponsDTO);
    }

    /**
     * Lista cupons de um cliente específico (apenas admin)
     * 
     * @param customerId ID do cliente
     * @return Lista de cupons do cliente
     */
    @Transactional(readOnly = true)
    public ResponseDTO<CouponResponseDTO> listCouponsByCustomerId(Long customerId) {
        // Valida que o cliente existe
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException(String.format("Cliente com ID %d não encontrado", customerId));
        }

        List<Coupon> coupons = couponRepository.findAllByCustomerId(customerId);

        List<CouponResponseDTO> couponsDTO = coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                String.format("Cupons do cliente %d encontrados com sucesso", customerId),
                couponsDTO);
    }

    /**
     * Lista todos os cupons promocionais (endpoint público)
     * Retorna apenas cupons ativos e não expirados
     * 
     * IMPORTANTE: Cupons promocionais podem ser usados múltiplas vezes até expirar.
     * Não filtra por valor disponível, pois mesmo que totalmente usados, ainda podem ser utilizados.
     * 
     * @return Lista de cupons promocionais disponíveis
     */
    @Transactional(readOnly = true)
    public ResponseDTO<CouponResponseDTO> listPromotionalCoupons() {
        List<Coupon> coupons = couponRepository.findActivePromotionalCoupons();

        List<CouponResponseDTO> couponsDTO = coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cupons promocionais encontrados com sucesso",
                couponsDTO);
    }

    /**
     * Obtém o cliente autenticado
     */
    private Customer getAuthenticatedCustomer() {
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return customerRepository.findById(customerAuth.id())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    }
}


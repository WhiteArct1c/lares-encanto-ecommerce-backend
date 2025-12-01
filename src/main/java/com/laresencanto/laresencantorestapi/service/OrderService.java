package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.address.Address;
import com.laresencanto.laresencantorestapi.domain.creditCard.CreditCard;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.order.*;
import com.laresencanto.laresencantorestapi.domain.product.Product;
import com.laresencanto.laresencantorestapi.domain.product.Stock;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.domain.coupon.Coupon;
import com.laresencanto.laresencantorestapi.domain.order.OrderCoupon;
import com.laresencanto.laresencantorestapi.dto.request.order.CouponUsageDTO;
import com.laresencanto.laresencantorestapi.dto.request.order.OrderCreateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.address.AddressResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.*;
import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ProductResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;
import com.laresencanto.laresencantorestapi.dto.request.shipping.ShippingCalculationRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.shipping.ShippingOptionResponseDTO;
import com.laresencanto.laresencantorestapi.exception.BusinessException;
import com.laresencanto.laresencantorestapi.exception.EntityNotFoundException;
import com.laresencanto.laresencantorestapi.dto.request.order.OrderStatusUpdateDTO;
import com.laresencanto.laresencantorestapi.repository.*;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class OrderService {

        private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

        private final OrderRepository orderRepository;
        private final OrderStatusRepository orderStatusRepository;
        private final StockRepository stockRepository;
        private final CustomerRepository customerRepository;
        private final AddressRepository addressRepository;
        private final CreditCardRepository creditCardRepository;
        private final ProductRepository productRepository;
        private final OrderPaymentRepository orderPaymentRepository;
        private final OrderShipmentRepository orderShipmentRepository;
        private final ShippingService shippingService;
        private final CouponService couponService;
        private final OrderCouponRepository orderCouponRepository;

        public OrderService(
                        OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
                        StockRepository stockRepository,
                        CustomerRepository customerRepository, AddressRepository addressRepository,
                        CreditCardRepository creditCardRepository, ProductRepository productRepository,
                        OrderPaymentRepository orderPaymentRepository, OrderShipmentRepository orderShipmentRepository,
                        ShippingService shippingService, CouponService couponService,
                        OrderCouponRepository orderCouponRepository) {
                this.orderRepository = orderRepository;
                this.orderStatusRepository = orderStatusRepository;
                this.stockRepository = stockRepository;
                this.customerRepository = customerRepository;
                this.addressRepository = addressRepository;
                this.creditCardRepository = creditCardRepository;
                this.productRepository = productRepository;
                this.orderPaymentRepository = orderPaymentRepository;
                this.orderShipmentRepository = orderShipmentRepository;
                this.shippingService = shippingService;
                this.couponService = couponService;
                this.orderCouponRepository = orderCouponRepository;
        }

        public ResponseDTO<OrderResponseDTO> listAllOrders() {
                List<OrderResponseDTO> orders = orderRepository.findAllWithRelations()
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos encontrados com sucesso",
                                orders);

        }

        public ResponseDTO<OrderResponseDTO> listPendingOrders() {
                // Status pendentes: EM PROCESSAMENTO, APROVADO, EM TRANSPORTE, ENTREGUE,
                // TROCA SOLICITADA, TROCA ACEITA, DEVOLUÇÃO SOLICITADA
                List<String> pendingStatuses = List.of(
                                "EM PROCESSAMENTO",
                                "APROVADO",
                                "EM TRANSPORTE",
                                "ENTREGUE",
                                "TROCA SOLICITADA",
                                "TROCA ACEITA",
                                "DEVOLUÇÃO SOLICITADA");

                List<OrderResponseDTO> orders = orderRepository.findAllByStatusNames(pendingStatuses)
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos pendentes encontrados com sucesso",
                                orders);
        }

        public ResponseDTO<OrderResponseDTO> listCanceledOrders() {
                // Status cancelados: REPROVADO, CANCELADO, TROCA RECUSADA, DEVOLUÇÃO RECUSADA
                List<String> canceledStatuses = List.of(
                                "REPROVADO",
                                "CANCELADO",
                                "TROCA RECUSADA",
                                "DEVOLUÇÃO RECUSADA");

                List<OrderResponseDTO> orders = orderRepository.findAllByStatusNames(canceledStatuses)
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos cancelados encontrados com sucesso",
                                orders);
        }

        public ResponseDTO<OrderResponseDTO> listFinishedOrders() {
                // Status finalizados: ENTREGUE, TROCA CONCLUÍDA, DEVOLUÇÃO CONCLUÍDA
                List<String> finishedStatuses = List.of(
                                "ENTREGUE",
                                "TROCA CONCLUÍDA",
                                "DEVOLUÇÃO CONCLUÍDA");

                List<OrderResponseDTO> orders = orderRepository.findAllByStatusNames(finishedStatuses)
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos finalizados encontrados com sucesso",
                                orders);
        }

        /**
         * Simula a validação de pagamento para um pedido específico
         * Aprova ou reprova baseado em uma lógica mockada
         * 
         * @param orderId ID do pedido a ser processado
         * @param approve true para aprovar, false para reprovar (opcional - se não
         *                informado, usa lógica automática)
         * @return Pedido atualizado
         */
        public ResponseDTO<OrderResponseDTO> mockPaymentValidation(Long orderId, Boolean approve) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

                if (!"EM PROCESSAMENTO".equals(order.getStatus().getName())) {
                        throw new BusinessException(
                                        String.format("Pedido não está em processamento. Status atual: %s",
                                                        order.getStatus().getName()));
                }

                // Se approve não foi informado, usa lógica mockada automática
                String newStatus;
                if (approve != null) {
                        // Força aprovação ou reprovação conforme solicitado
                        newStatus = approve ? "APROVADO" : "REPROVADO";
                } else {
                        // Lógica mockada: aprova se valor <= 5000, reprova se > 5000
                        // (pode ser alterada para qualquer outra lógica)
                        newStatus = order.getTotalPrice() <= 5000.0 ? "APROVADO" : "REPROVADO";
                }

                OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(orderId, newStatus);
                return updateOrderStatus(statusUpdate);
        }

        /**
         * Processa webhook de pagamento (simula como um gateway real enviaria)
         * Este método simula como um gateway de pagamento (Stripe, Mercado Pago, etc)
         * notificaria o sistema sobre o status do pagamento
         * 
         * @param webhookDTO Dados do webhook de pagamento
         * @return Pedido atualizado
         */
        public ResponseDTO<OrderResponseDTO> processPaymentWebhook(
                        com.laresencanto.laresencantorestapi.dto.request.payment.PaymentWebhookDTO webhookDTO) {
                Order order = orderRepository.findById(webhookDTO.orderId())
                                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

                if (!"EM PROCESSAMENTO".equals(order.getStatus().getName())) {
                        throw new BusinessException(
                                        String.format("Pedido não está em processamento. Status atual: %s",
                                                        order.getStatus().getName()));
                }

                // Converte status do webhook para status do pedido
                String newStatus;
                switch (webhookDTO.paymentStatus().toUpperCase()) {
                        case "APPROVED":
                        case "PAID":
                        case "SUCCESS":
                                newStatus = "APROVADO";
                                break;
                        case "REJECTED":
                        case "FAILED":
                        case "DECLINED":
                                newStatus = "REPROVADO";
                                break;
                        case "PENDING":
                        default:
                                // Mantém em processamento
                                return new ResponseDTO<>(
                                                HttpStatus.OK.toString(),
                                                "Pagamento ainda está pendente",
                                                List.of(convertToOrderResponseDTO(order)));
                }

                OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(webhookDTO.orderId(), newStatus);
                return updateOrderStatus(statusUpdate);
        }

        /**
         * Processa todos os pedidos pendentes automaticamente
         * Simula validação de pagamento para todos os pedidos em processamento
         * 
         * @param approveAll true para aprovar todos, false para reprovar todos, null
         *                   para usar lógica automática
         * @return Lista de pedidos processados
         */
        public ResponseDTO<OrderResponseDTO> processAllPendingOrders(Boolean approveAll) {
                List<Order> pendingOrders = orderRepository.findAllByStatusName("EM PROCESSAMENTO");

                if (pendingOrders.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.OK.toString(),
                                        "Nenhum pedido pendente encontrado",
                                        List.of());
                }

                List<OrderResponseDTO> processedOrders = new ArrayList<>();

                for (Order order : pendingOrders) {
                        String newStatus;
                        if (approveAll != null) {
                                // Força aprovação ou reprovação conforme solicitado
                                newStatus = approveAll ? "APROVADO" : "REPROVADO";
                        } else {
                                // Lógica mockada: aprova se valor <= 5000, reprova se > 5000
                                newStatus = order.getTotalPrice() <= 5000.0 ? "APROVADO" : "REPROVADO";
                        }

                        OrderStatusUpdateDTO statusUpdate = new OrderStatusUpdateDTO(order.getId(), newStatus);
                        ResponseDTO<OrderResponseDTO> result = updateOrderStatus(statusUpdate);
                        if (result.data() != null && !result.data().isEmpty()) {
                                processedOrders.add(result.data().get(0));
                        }
                }

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                String.format("%d pedido(s) processado(s) com sucesso", processedOrders.size()),
                                processedOrders);
        }

        public ResponseDTO<OrderResponseDTO> listCustomerOrders() {
                Customer customer = getAuthenticatedCustomer();

                List<OrderResponseDTO> orders = orderRepository.findAllByCustomerId(customer.getId())
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos encontrados com sucesso",
                                orders);
        }

        public ResponseDTO<OrderResponseDTO> createOrder(OrderCreateRequestDTO requestDTO) {
                logger.info("=== INÍCIO createOrder ===");
                logger.info("Request recebido - totalPrice: {}, shipping: {}, coupons: {}, orderProducts size: {}",
                                requestDTO.totalPrice(),
                                requestDTO.shipping() != null ? requestDTO.shipping().price() : "null",
                                requestDTO.coupons() != null ? requestDTO.coupons().size() : 0,
                                requestDTO.orderProducts() != null ? requestDTO.orderProducts().size() : 0);

                // 1. Validação e obtenção do cliente
                Customer customer = getAuthenticatedCustomer();
                logger.info("Cliente autenticado - ID: {}, Nome: {}", customer.getId(), customer.getFullName());

                // 2. Calcula o valor total real do pedido (produtos + frete)
                Double totalOrderValue = calculateTotalOrderValue(requestDTO);
                logger.info("Valor total do pedido calculado: R$ {}", String.format("%.2f", totalOrderValue));

                // 3. Validações antes de criar o pedido
                BigDecimal totalCouponDiscount = validateAndCalculateCoupons(requestDTO.coupons(), customer.getId(),
                                totalOrderValue);
                logger.info("Desconto total de cupons calculado: R$ {}",
                                String.format("%.2f", totalCouponDiscount.doubleValue()));

                validatePayments(requestDTO.orderPayments(), totalOrderValue, totalCouponDiscount);
                validateProductsStock(requestDTO.orderProducts());

                // 3. Construção do pedido
                Order order = buildOrder(requestDTO, customer);

                // 4. Persistência e retorno
                try {
                        Order savedOrder = orderRepository.save(order);
                        logger.info("Pedido criado com sucesso - ID: {}, Total: R$ {}, Desconto: R$ {}",
                                        savedOrder.getId(),
                                        String.format("%.2f", totalOrderValue),
                                        String.format("%.2f", totalCouponDiscount.doubleValue()));
                        logger.info("=== FIM createOrder (SUCESSO) ===");
                        return buildSuccessResponse(savedOrder);
                } catch (BusinessException e) {
                        logger.error("Erro de negócio ao criar pedido: {}", e.getMessage());
                        logger.info("=== FIM createOrder (ERRO NEGÓCIO) ===");
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
                } catch (Exception e) {
                        logger.error("Erro inesperado ao criar pedido", e);
                        logger.info("=== FIM createOrder (ERRO INTERNO) ===");
                        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar o pedido");
                }
        }

        // Métodos auxiliares privados para melhor organização
        private Customer getAuthenticatedCustomer() {
                CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                return customerRepository.findById(customerAuth.id())
                                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        }

        private Order buildOrder(OrderCreateRequestDTO requestDTO, Customer customer) {
                Order order = new Order();

                order.setCustomer(customer);
                order.setType(requestDTO.type());
                order.setTotalPrice(requestDTO.totalPrice());
                order.setStatus(getDefaultOrderStatus());

                // Configuração do endereço
                order.setAddress(buildAddress(customer, order, requestDTO.address()));

                // Salva o pedido primeiro para ter o ID
                Order savedOrder = orderRepository.save(order);

                // Configuração dos items do pedido (ANTES dos pagamentos e envio)
                List<OrderProduct> orderProducts = buildOrderProducts(savedOrder,
                                requestDTO.orderProducts().stream().toList());
                savedOrder.setOrderProducts(orderProducts);

                // Configuração dos pagamentos
                savedOrder.setOrderPayments(
                                buildOrderPayments(customer, savedOrder, requestDTO.orderPayments().stream().toList()));

                // Configuração do envio (calcula automaticamente baseado nos produtos e
                // endereço)
                savedOrder.setOrderShipment(buildOrderShipment(savedOrder, requestDTO.shipping(), requestDTO.address(),
                                requestDTO.orderProducts()));

                // Salva novamente para persistir os relacionamentos
                Order finalOrder = orderRepository.save(savedOrder);

                // Processa cupons utilizados (após salvar o pedido)
                if (requestDTO.coupons() != null && !requestDTO.coupons().isEmpty()) {
                        processCoupons(finalOrder, requestDTO.coupons(), customer.getId());
                }

                return finalOrder;
        }

        private Address buildAddress(Customer customer, Order order, AddressRequestDTO addressDTO) {
                if (addressDTO.id() == null || addressDTO.id().isEmpty()) {
                        Address newAddress = new Address();

                        newAddress.setTitle(addressDTO.title());
                        newAddress.setCep(addressDTO.cep());
                        newAddress.setResidenceType(addressDTO.residenceType());
                        newAddress.setAddressType(addressDTO.addressType());
                        newAddress.setCategories(mapAddressCategories(addressDTO.addressCategories()));
                        newAddress.setStreetName(addressDTO.streetName());
                        newAddress.setAddressNumber(addressDTO.addressNumber());
                        newAddress.setNeighborhoods(addressDTO.neighborhoods());
                        newAddress.setCity(addressDTO.city());
                        newAddress.setState(addressDTO.state());
                        newAddress.setCountry(addressDTO.country());
                        newAddress.setObservations(addressDTO.observations());
                        newAddress.setCustomer(customer);

                        Address savedAddress = addressRepository.save(newAddress);
                        order.setAddress(savedAddress);

                        return savedAddress;
                } else {
                        Address address = addressRepository.findById(Long.valueOf(addressDTO.id()))
                                        .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));
                        order.setAddress(address);

                        return address;
                }
        }

        private Set<AddressCategory> mapAddressCategories(List<String> categories) {
                return categories.stream()
                                .map(AddressCategory::fromString)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());
        }

        /**
         * Calcula o valor total real do pedido incluindo produtos e frete
         * 
         * IMPORTANTE: Sempre calcula os produtos a partir dos orderProducts para evitar
         * problemas quando o frontend envia totalPrice já incluindo frete.
         * O totalPrice do requestDTO é ignorado quando há shipping, pois pode estar
         * inconsistente (alguns frontends enviam totalPrice já com frete, outros não).
         */
        private Double calculateTotalOrderValue(OrderCreateRequestDTO requestDTO) {
                logger.debug("=== INÍCIO calculateTotalOrderValue ===");
                logger.debug("totalPrice do requestDTO: {}", requestDTO.totalPrice());
                logger.debug("shipping do requestDTO: {}", requestDTO.shipping());
                logger.debug("orderProducts size: {}",
                                requestDTO.orderProducts() != null ? requestDTO.orderProducts().size() : 0);

                Double productsTotal = 0.0;

                // SEMPRE calcula a partir dos produtos para garantir precisão
                // Ignora totalPrice do requestDTO pois pode estar inconsistente (com ou sem
                // frete)
                if (requestDTO.orderProducts() != null && !requestDTO.orderProducts().isEmpty()) {
                        productsTotal = requestDTO.orderProducts().stream()
                                        .mapToDouble(product -> {
                                                if (product.product() != null
                                                                && product.product().salePrice() != null) {
                                                        Double itemTotal = product.product().salePrice()
                                                                        .doubleValue()
                                                                        * product.quantity();
                                                        logger.debug("Produto: {} - salePrice: R$ {}, quantity: {}, itemTotal: R$ {}",
                                                                        product.product().name(),
                                                                        String.format("%.2f", product.product()
                                                                                        .salePrice()
                                                                                        .doubleValue()),
                                                                        product.quantity(),
                                                                        String.format("%.2f", itemTotal));
                                                        return itemTotal;
                                                }
                                                logger.warn("Produto sem salePrice ou product null - quantity: {}",
                                                                product.quantity());
                                                return 0.0;
                                        })
                                        .sum();
                        logger.debug("Total calculado a partir dos produtos: R$ {}",
                                        String.format("%.2f", productsTotal));
                } else {
                        logger.warn("orderProducts está null ou vazio, tentando usar totalPrice como fallback");
                        // Fallback: se não há produtos, usa totalPrice (mas isso não deveria acontecer)
                        if (requestDTO.totalPrice() != null && requestDTO.totalPrice() > 0) {
                                productsTotal = requestDTO.totalPrice();
                                logger.warn("Usando totalPrice como fallback: R$ {}",
                                                String.format("%.2f", productsTotal));
                        } else {
                                logger.error("Não foi possível calcular o total dos produtos!");
                        }
                }

                // Adiciona o valor do frete se estiver presente
                Double shippingPrice = 0.0;
                if (requestDTO.shipping() != null && requestDTO.shipping().price() != null) {
                        shippingPrice = requestDTO.shipping().price();
                        logger.debug("Frete encontrado: R$ {}", String.format("%.2f", shippingPrice));
                } else {
                        logger.debug("Frete não presente ou price null");
                }

                Double totalValue = productsTotal + shippingPrice;
                logger.info("RESULTADO calculateTotalOrderValue - Produtos: R$ {}, Frete: R$ {}, Total: R$ {}",
                                String.format("%.2f", productsTotal),
                                String.format("%.2f", shippingPrice),
                                String.format("%.2f", totalValue));
                logger.debug("=== FIM calculateTotalOrderValue ===");
                return totalValue;
        }

        /**
         * Valida e calcula o desconto total dos cupons
         * RN0033 - Apenas um cupom promocional por compra (mas permite múltiplos cupons
         * de troca)
         * 
         * @param coupons    Lista de cupons a serem utilizados
         * @param customerId ID do cliente
         * @param totalPrice Valor total do pedido (para validar que desconto não excede
         *                   o total)
         * @return Desconto total calculado (não pode exceder o total do pedido)
         */
        private BigDecimal validateAndCalculateCoupons(List<CouponUsageDTO> coupons, Long customerId,
                        Double totalPrice) {
                logger.debug("=== INÍCIO validateAndCalculateCoupons ===");
                logger.debug("totalPrice recebido: R$ {}", String.format("%.2f", totalPrice));
                logger.debug("coupons size: {}", coupons != null ? coupons.size() : 0);

                if (coupons == null || coupons.isEmpty()) {
                        logger.debug("Nenhum cupom informado, retornando desconto zero");
                        logger.debug("=== FIM validateAndCalculateCoupons (sem cupons) ===");
                        return BigDecimal.ZERO;
                }

                // RN0033 - Valida apenas um cupom promocional
                logger.debug("Validando quantidade de cupons promocionais...");
                long promotionalCount = coupons.stream()
                                .filter(c -> {
                                        try {
                                                Coupon coupon = couponService.validateCoupon(c.couponCode(),
                                                                customerId);
                                                boolean isPromotional = "PROMOTIONAL".equals(coupon.getCouponType());
                                                logger.debug("Cupom {} - Tipo: {}, É promocional: {}", c.couponCode(),
                                                                coupon.getCouponType(), isPromotional);
                                                return isPromotional;
                                        } catch (Exception e) {
                                                logger.warn("Erro ao validar cupom {}: {}", c.couponCode(),
                                                                e.getMessage());
                                                return false;
                                        }
                                })
                                .count();

                logger.debug("Total de cupons promocionais encontrados: {}", promotionalCount);
                if (promotionalCount > 1) {
                        logger.error("Violação RN0033: Mais de um cupom promocional detectado");
                        throw new BusinessException("Apenas um cupom promocional pode ser utilizado por compra");
                }

                // Valida todos os cupons e calcula desconto total
                BigDecimal totalDiscount = BigDecimal.ZERO;
                logger.debug("Processando {} cupons...", coupons.size());
                for (CouponUsageDTO couponUsage : coupons) {
                        logger.debug("Processando cupom: {} - Valor a usar: R$ {}", couponUsage.couponCode(),
                                        String.format("%.2f", couponUsage.amountToUse().doubleValue()));

                        Coupon coupon = couponService.validateCoupon(couponUsage.couponCode(), customerId);
                        BigDecimal availableValue = coupon.getAvailableValue();

                        logger.debug("Cupom {} - Valor disponível: R$ {}, Valor solicitado: R$ {}, Tipo: {}",
                                        coupon.getCode(),
                                        String.format("%.2f", availableValue.doubleValue()),
                                        String.format("%.2f", couponUsage.amountToUse().doubleValue()),
                                        coupon.getCouponType());

                        // Validação para cupons de troca: verifica valor disponível
                        if ("EXCHANGE".equals(coupon.getCouponType())) {
                                if (couponUsage.amountToUse().compareTo(availableValue) > 0) {
                                        logger.error("Valor solicitado excede disponível - Cupom: {}, Solicitado: R$ {}, Disponível: R$ {}",
                                                        coupon.getCode(),
                                                        String.format("%.2f", couponUsage.amountToUse().doubleValue()),
                                                        String.format("%.2f", availableValue.doubleValue()));
                                        throw new BusinessException(
                                                        String.format("Valor solicitado (R$ %.2f) do cupom %s é maior que o disponível (R$ %.2f)",
                                                                        couponUsage.amountToUse(), coupon.getCode(),
                                                                        availableValue));
                                }
                        }

                        // Validação para cupons promocionais: valor usado não pode exceder o valor
                        // original
                        // Cupons promocionais podem ser usados múltiplas vezes, mas cada uso é limitado
                        // ao valor original
                        if ("PROMOTIONAL".equals(coupon.getCouponType())) {
                                BigDecimal couponValue = coupon.getValue();
                                if (couponUsage.amountToUse().compareTo(couponValue) > 0) {
                                        logger.error("Valor solicitado excede valor máximo do cupom promocional - Cupom: {}, Solicitado: R$ {}, Valor máximo: R$ {}",
                                                        coupon.getCode(),
                                                        String.format("%.2f", couponUsage.amountToUse().doubleValue()),
                                                        String.format("%.2f", couponValue.doubleValue()));
                                        throw new BusinessException(
                                                        String.format("Valor solicitado (R$ %.2f) excede o valor máximo do cupom promocional %s (R$ %.2f). "
                                                                        +
                                                                        "Cupons promocionais podem ser usados múltiplas vezes, mas cada uso é limitado ao valor original do cupom.",
                                                                        couponUsage.amountToUse(), coupon.getCode(),
                                                                        couponValue));
                                }
                        }

                        totalDiscount = totalDiscount.add(couponUsage.amountToUse());
                        logger.debug("Desconto parcial acumulado: R$ {}",
                                        String.format("%.2f", totalDiscount.doubleValue()));
                }

                // Valida que o desconto não excede o valor total do pedido
                BigDecimal totalPriceBD = BigDecimal.valueOf(totalPrice);
                logger.info("Validação final - Total do pedido: R$ {}, Desconto total: R$ {}",
                                String.format("%.2f", totalPrice),
                                String.format("%.2f", totalDiscount.doubleValue()));

                if (totalDiscount.compareTo(totalPriceBD) > 0) {
                        logger.error("ERRO: Desconto excede total do pedido - Desconto: R$ {}, Total: R$ {}",
                                        String.format("%.2f", totalDiscount.doubleValue()),
                                        String.format("%.2f", totalPrice));
                        throw new BusinessException(
                                        String.format("O desconto total dos cupons (R$ %.2f) não pode ser maior que o valor total do pedido (R$ %.2f)",
                                                        totalDiscount.doubleValue(), totalPrice));
                }

                logger.info("RESULTADO validateAndCalculateCoupons - Desconto total válido: R$ {}",
                                String.format("%.2f", totalDiscount.doubleValue()));
                logger.debug("=== FIM validateAndCalculateCoupons ===");
                return totalDiscount;
        }

        /**
         * Valida pagamentos considerando desconto de cupons
         * RN0035 - Considerar sempre o valor máximo dos cupons primeiro
         * Permite-se cartão < R$ 10,00 quando há cupons
         * 
         * Se o desconto dos cupons zerar ou exceder o valor total, não é necessário
         * pagamento
         */
        private void validatePayments(Set<OrderPaymentResponseDTO> payments, Double totalPrice,
                        BigDecimal couponDiscount) {
                logger.debug("=== INÍCIO validatePayments ===");
                logger.debug("totalPrice: R$ {}, couponDiscount: R$ {}, payments size: {}",
                                String.format("%.2f", totalPrice),
                                String.format("%.2f", couponDiscount.doubleValue()),
                                payments != null ? payments.size() : 0);

                BigDecimal totalPriceBD = BigDecimal.valueOf(totalPrice);
                BigDecimal amountToPay = totalPriceBD.subtract(couponDiscount);

                logger.info("Cálculo amountToPay - Total: R$ {}, Desconto: R$ {}, A pagar: R$ {}",
                                String.format("%.2f", totalPrice),
                                String.format("%.2f", couponDiscount.doubleValue()),
                                String.format("%.2f", amountToPay.doubleValue()));

                // Se o valor a pagar é zero ou negativo (desconto >= total), não precisa de
                // pagamento
                if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
                        logger.info("Valor a pagar é zero ou negativo (desconto >= total)");
                        // Se não há pagamentos, está correto
                        if (payments == null || payments.isEmpty()) {
                                logger.debug("Nenhum pagamento informado - OK (pedido totalmente coberto por cupons)");
                                logger.debug("=== FIM validatePayments (sem pagamentos necessários) ===");
                                return;
                        }
                        // Se há pagamentos mas o valor a pagar é zero, valida que a soma também é zero
                        BigDecimal totalPayments = payments.stream()
                                        .map(p -> {
                                                BigDecimal paymentTotal = p.installmentValue()
                                                                .multiply(BigDecimal.valueOf(p.installments()));
                                                logger.debug("Pagamento - Parcela: R$ {}, Parcelas: {}, Total: R$ {}",
                                                                String.format("%.2f",
                                                                                p.installmentValue().doubleValue()),
                                                                p.installments(),
                                                                String.format("%.2f", paymentTotal.doubleValue()));
                                                return paymentTotal;
                                        })
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        logger.debug("Total de pagamentos informados: R$ {}",
                                        String.format("%.2f", totalPayments.doubleValue()));

                        if (totalPayments.compareTo(BigDecimal.ZERO) != 0) {
                                logger.error("ERRO: Pagamentos informados quando pedido está totalmente coberto por cupons");
                                throw new BusinessException(
                                                String.format("O pedido está totalmente coberto por cupons (desconto: R$ %.2f). Não é necessário informar pagamentos.",
                                                                couponDiscount.doubleValue()));
                        }
                        logger.debug("=== FIM validatePayments (pagamentos zero OK) ===");
                        return;
                }

                // Se há valor a pagar, é necessário informar pagamentos
                if (payments == null || payments.isEmpty()) {
                        logger.error("ERRO: Valor a pagar > 0 mas nenhum pagamento informado");
                        throw new BusinessException(
                                        String.format("É necessário informar pelo menos uma forma de pagamento. Valor a pagar: R$ %.2f",
                                                        amountToPay.doubleValue()));
                }

                BigDecimal totalPayments = payments.stream()
                                .map(p -> {
                                        BigDecimal paymentTotal = p.installmentValue()
                                                        .multiply(BigDecimal.valueOf(p.installments()));
                                        logger.debug("Pagamento - Parcela: R$ {}, Parcelas: {}, Total: R$ {}",
                                                        String.format("%.2f", p.installmentValue().doubleValue()),
                                                        p.installments(),
                                                        String.format("%.2f", paymentTotal.doubleValue()));
                                        return paymentTotal;
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                logger.info("Validação de pagamentos - Total pagamentos: R$ {}, A pagar: R$ {}",
                                String.format("%.2f", totalPayments.doubleValue()),
                                String.format("%.2f", amountToPay.doubleValue()));

                // Validar se soma dos pagamentos confere com o valor após desconto de cupons
                if (totalPayments.compareTo(amountToPay) != 0) {
                        BigDecimal difference = amountToPay.subtract(totalPayments);
                        logger.error("ERRO: Soma dos pagamentos não confere - Pagamentos: R$ {}, A pagar: R$ {}, Diferença: R$ {}",
                                        String.format("%.2f", totalPayments.doubleValue()),
                                        String.format("%.2f", amountToPay.doubleValue()),
                                        String.format("%.2f", difference.doubleValue()));

                        String errorMessage;
                        if (difference.compareTo(BigDecimal.ZERO) > 0) {
                                // Faltam pagamentos
                                errorMessage = String.format(
                                                "A soma dos pagamentos (R$ %.2f) não confere com o valor a pagar (R$ %.2f). Faltam R$ %.2f. "
                                                                +
                                                                "Lembre-se: o valor a pagar inclui produtos + frete. Total do pedido: R$ %.2f, Desconto de cupons: R$ %.2f",
                                                totalPayments.doubleValue(), amountToPay.doubleValue(),
                                                difference.doubleValue(),
                                                totalPrice, couponDiscount.doubleValue());
                        } else {
                                // Pagamentos excedem
                                errorMessage = String.format(
                                                "A soma dos pagamentos (R$ %.2f) não confere com o valor a pagar (R$ %.2f). Excede em R$ %.2f. "
                                                                +
                                                                "Total do pedido: R$ %.2f, Desconto de cupons: R$ %.2f",
                                                totalPayments.doubleValue(), amountToPay.doubleValue(),
                                                difference.abs().doubleValue(),
                                                totalPrice, couponDiscount.doubleValue());
                        }

                        throw new BusinessException(errorMessage);
                }

                // Validar valor mínimo por cartão (R$ 10,00)
                // RN0035 - Exceção: permite cartão < R$ 10,00 quando há cupons
                BigDecimal minimumValue = new BigDecimal("10.00");
                boolean hasCoupons = couponDiscount.compareTo(BigDecimal.ZERO) > 0;
                logger.debug("Validação valor mínimo cartão - Tem cupons: {}, Mínimo: R$ {}", hasCoupons,
                                String.format("%.2f", minimumValue.doubleValue()));

                for (OrderPaymentResponseDTO payment : payments) {
                        if (payment.creditCard() != null) {
                                BigDecimal paymentTotal = payment.installmentValue()
                                                .multiply(BigDecimal.valueOf(payment.installments()));

                                logger.debug("Validando cartão - Total: R$ {}, Mínimo: R$ {}, Tem cupons: {}",
                                                String.format("%.2f", paymentTotal.doubleValue()),
                                                String.format("%.2f", minimumValue.doubleValue()),
                                                hasCoupons);

                                // Se não há cupons, valida mínimo normalmente
                                // Se há cupons, permite valor menor (RN0035)
                                if (!hasCoupons && paymentTotal.compareTo(minimumValue) < 0) {
                                        logger.error("ERRO: Valor do cartão abaixo do mínimo - Valor: R$ {}, Mínimo: R$ {}",
                                                        String.format("%.2f", paymentTotal.doubleValue()),
                                                        String.format("%.2f", minimumValue.doubleValue()));
                                        throw new BusinessException(
                                                        String.format("O valor mínimo por cartão de crédito é R$ 10,00. Valor informado: R$ %.2f",
                                                                        paymentTotal.doubleValue()));
                                }
                        }
                }

                logger.info("RESULTADO validatePayments - Validação OK - Total pagamentos: R$ {}, A pagar: R$ {}",
                                String.format("%.2f", totalPayments.doubleValue()),
                                String.format("%.2f", amountToPay.doubleValue()));
                logger.debug("=== FIM validatePayments ===");
        }

        /**
         * Processa e registra o uso dos cupons no pedido
         */
        private void processCoupons(Order order, List<CouponUsageDTO> coupons, Long customerId) {
                for (CouponUsageDTO couponUsage : coupons) {
                        Coupon coupon = couponService.validateCoupon(couponUsage.couponCode(), customerId);

                        // Usa o cupom (atualiza usedValue e desativa se necessário)
                        couponService.useCoupon(coupon, couponUsage.amountToUse());

                        // Registra o uso do cupom no pedido
                        OrderCoupon orderCoupon = new OrderCoupon();
                        orderCoupon.setOrder(order);
                        orderCoupon.setCoupon(coupon);
                        orderCoupon.setAmountUsed(couponUsage.amountToUse());
                        orderCouponRepository.save(orderCoupon);
                }
        }

        private void validateProductsStock(Set<OrderProductResponseDTO> products) {
                if (products == null || products.isEmpty()) {
                        throw new BusinessException("É necessário informar pelo menos um produto no pedido");
                }

                for (OrderProductResponseDTO productDTO : products) {
                        Product product = productRepository
                                        .findAvailableProductById(Long.valueOf(productDTO.product().id()))
                                        .orElseThrow(() -> new BusinessException(
                                                        String.format("Produto com ID %s não encontrado ou indisponível",
                                                                        productDTO.product().id())));

                        Stock stock = stockRepository.findByProductId(product.getId())
                                        .orElseThrow(() -> new BusinessException(
                                                        String.format("Estoque do produto %s não encontrado",
                                                                        product.getName())));

                        int availableQuantity = stock.getQuantity() - stock.getReservedQuantity();
                        int requestedQuantity = productDTO.quantity();

                        if (requestedQuantity <= 0) {
                                throw new BusinessException(
                                                String.format("A quantidade solicitada para o produto %s deve ser maior que zero",
                                                                product.getName()));
                        }

                        if (requestedQuantity > availableQuantity) {
                                throw new BusinessException(
                                                String.format("Quantidade solicitada (%d) para o produto %s é maior que a disponível (%d)",
                                                                requestedQuantity, product.getName(),
                                                                availableQuantity));
                        }

                        if (availableQuantity <= 0) {
                                throw new BusinessException(
                                                String.format("Produto %s está sem estoque disponível",
                                                                product.getName()));
                        }
                }
        }

        private Set<OrderPayment> buildOrderPayments(Customer customer, Order order,
                        List<OrderPaymentResponseDTO> paymentDTOs) {
                return paymentDTOs.stream()
                                .map(paymentDTO -> buildOrderPayment(customer, order, paymentDTO))
                                .collect(Collectors.toSet());
        }

        /**
         * Constrói um OrderPayment, permitindo usar um cartão existente ou criar um
         * novo
         * 
         * Regras:
         * - Se creditCard.id for fornecido: valida que o cartão pertence ao cliente e
         * usa ele
         * - Se creditCard.id for null: cria um novo cartão com os dados fornecidos
         * - Valida os dados do cartão quando é novo
         */
        private OrderPayment buildOrderPayment(Customer customer, Order order, OrderPaymentResponseDTO paymentDTO) {
                logger.debug("=== INÍCIO buildOrderPayment ===");
                logger.debug("PaymentDTO - installments: {}, installmentValue: {}, paymentMethod: {}",
                                paymentDTO.installments(), paymentDTO.installmentValue(), paymentDTO.paymentMethod());
                logger.debug("CreditCard - id: {}, cardNumber: {}, cardName: {}, cardFlag: {}",
                                paymentDTO.creditCard() != null ? paymentDTO.creditCard().id() : "null",
                                paymentDTO.creditCard() != null ? paymentDTO.creditCard().cardNumber() : "null",
                                paymentDTO.creditCard() != null ? paymentDTO.creditCard().cardName() : "null",
                                paymentDTO.creditCard() != null ? paymentDTO.creditCard().cardFlag() : "null");

                CreditCard orderCreditCard;

                // Se o ID do cartão foi fornecido, valida que ele pertence ao cliente
                if (paymentDTO.creditCard() != null && paymentDTO.creditCard().id() != null) {
                        logger.debug("Cartão existente informado - ID: {}", paymentDTO.creditCard().id());

                        // Busca o cartão e valida que pertence ao cliente
                        orderCreditCard = creditCardRepository.findByIdAndCustomerId(
                                        paymentDTO.creditCard().id(), customer.getId())
                                        .orElseThrow(() -> new BusinessException(
                                                        String.format("Cartão de crédito com ID %d não encontrado ou não pertence ao cliente autenticado",
                                                                        paymentDTO.creditCard().id())));

                        logger.info("Cartão existente validado - ID: {}, Nome: {}, Bandeira: {}",
                                        orderCreditCard.getId(), orderCreditCard.getCardName(),
                                        orderCreditCard.getCardFlag());
                } else {
                        // Cria um novo cartão
                        logger.debug("Criando novo cartão de crédito no momento do pagamento");

                        if (paymentDTO.creditCard() == null) {
                                throw new BusinessException(
                                                "Dados do cartão de crédito são obrigatórios quando não é informado um cartão existente");
                        }

                        // Valida que os dados obrigatórios estão presentes
                        if (paymentDTO.creditCard().cardNumber() == null
                                        || paymentDTO.creditCard().cardNumber().isEmpty()) {
                                throw new BusinessException(
                                                "Número do cartão é obrigatório ao cadastrar um novo cartão");
                        }
                        if (paymentDTO.creditCard().cardName() == null
                                        || paymentDTO.creditCard().cardName().isEmpty()) {
                                throw new BusinessException("Nome no cartão é obrigatório ao cadastrar um novo cartão");
                        }
                        if (paymentDTO.creditCard().cardCode() == null
                                        || paymentDTO.creditCard().cardCode().isEmpty()) {
                                throw new BusinessException(
                                                "Código de segurança do cartão é obrigatório ao cadastrar um novo cartão");
                        }
                        if (paymentDTO.creditCard().cardFlag() == null
                                        || paymentDTO.creditCard().cardFlag().isEmpty()) {
                                throw new BusinessException(
                                                "Bandeira do cartão é obrigatória ao cadastrar um novo cartão");
                        }

                        // Cria o novo cartão
                        orderCreditCard = new CreditCard();
                        orderCreditCard.setCardNumber(paymentDTO.creditCard().cardNumber());
                        orderCreditCard.setCardName(paymentDTO.creditCard().cardName());
                        orderCreditCard.setCardCode(paymentDTO.creditCard().cardCode());
                        orderCreditCard.setCardFlag(paymentDTO.creditCard().cardFlag());
                        orderCreditCard.setMainCard(paymentDTO.creditCard().mainCard());
                        orderCreditCard.setCustomer(customer);

                        // Salva o novo cartão
                        orderCreditCard = creditCardRepository.save(orderCreditCard);
                        logger.info("Novo cartão criado com sucesso - ID: {}, Nome: {}, Bandeira: {}",
                                        orderCreditCard.getId(), orderCreditCard.getCardName(),
                                        orderCreditCard.getCardFlag());
                }

                // Cria o pagamento
                OrderPayment payment = new OrderPayment();
                payment.setInstallments(paymentDTO.installments());
                payment.setInstallmentValue(paymentDTO.installmentValue());
                payment.setMethod(OrderPayment.PaymentMethod.valueOf(paymentDTO.paymentMethod()));
                payment.setCreditCard(orderCreditCard);
                payment.setOrder(order);

                OrderPayment savedPayment = orderPaymentRepository.save(payment);
                logger.debug("Pagamento criado com sucesso - ID: {}, Método: {}, Parcelas: {}, Valor parcela: R$ {}",
                                savedPayment.getId(), savedPayment.getMethod(), savedPayment.getInstallments(),
                                String.format("%.2f", savedPayment.getInstallmentValue().doubleValue()));
                logger.debug("=== FIM buildOrderPayment ===");

                return savedPayment;
        }

        private List<OrderProduct> buildOrderProducts(Order order, List<OrderProductResponseDTO> productDTOs) {
                return productDTOs.stream()
                                .map(productDTO -> buildOrderProduct(order, productDTO))
                                .collect(Collectors.toList());
        }

        private OrderProduct buildOrderProduct(Order order, OrderProductResponseDTO productDTO) {
                Product product = productRepository.findAvailableProductById(Long.valueOf(productDTO.product().id()))
                                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
                Stock stock = stockRepository.findByProductId(product.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Estoque deste produto não encontrado"));

                // Validação adicional de estoque antes de reservar
                int availableQuantity = stock.getQuantity() - stock.getReservedQuantity();
                if (productDTO.quantity() > availableQuantity) {
                        throw new BusinessException(
                                        String.format("Quantidade solicitada (%d) para o produto %s é maior que a disponível (%d)",
                                                        productDTO.quantity(), product.getName(), availableQuantity));
                }

                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setQuantity(productDTO.quantity());
                // Armazena o preço de venda do produto no momento da compra
                // Isso é importante para calcular corretamente o valor do cupom de troca depois
                orderProduct.setUnitPrice(product.getSalePrice());
                orderProduct.setProduct(product);
                orderProduct.setOrder(order);

                // Reserva o estoque
                stock.setReservedQuantity(stock.getReservedQuantity() + productDTO.quantity());
                stockRepository.save(stock);

                return orderProduct;
        }

        private OrderShipment buildOrderShipment(Order order, OrderShipmentResponseDTO shipmentDTO,
                        AddressRequestDTO addressDTO, Set<OrderProductResponseDTO> products) {
                // Se o shipmentDTO contém um ID, calcula o frete automaticamente
                // Caso contrário, usa os dados fornecidos (compatibilidade com versão antiga)
                String name;
                String deliveryTime;
                Double price;

                if (shipmentDTO.id() != null) {
                        // Calcula o frete automaticamente baseado nos produtos e endereço
                        ShippingCalculationRequestDTO calculationRequest = new ShippingCalculationRequestDTO(
                                        addressDTO,
                                        products);

                        List<ShippingOptionResponseDTO> options = shippingService
                                        .calculateShippingOptions(calculationRequest);

                        // Encontra a opção selecionada pelo ID
                        ShippingOptionResponseDTO calculatedShipping = options.stream()
                                        .filter(opt -> opt.id().equals(shipmentDTO.id()))
                                        .findFirst()
                                        .orElseThrow(() -> new BusinessException(
                                                        String.format("Opção de frete com ID %d não encontrada ou não disponível para este pedido",
                                                                        shipmentDTO.id())));

                        name = calculatedShipping.name();
                        deliveryTime = calculatedShipping.deliveryTime();
                        price = calculatedShipping.price();
                } else {
                        // Usa os dados fornecidos diretamente (compatibilidade)
                        name = shipmentDTO.name();
                        deliveryTime = shipmentDTO.deliveryTime();
                        price = shipmentDTO.price();
                }

                OrderShipment shipment = new OrderShipment();
                shipment.setName(name);
                shipment.setDeliveryTime(deliveryTime);
                shipment.setPrice(price);
                shipment.setOrder(order);

                return orderShipmentRepository.save(shipment);
        }

        private OrderStatus getDefaultOrderStatus() {
                return orderStatusRepository.findByName("EM PROCESSAMENTO")
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Status padrão 'EM PROCESSAMENTO' não encontrado"));
        }

        private ResponseDTO<OrderResponseDTO> buildSuccessResponse(Order order) {
                return new ResponseDTO<>(
                                HttpStatus.CREATED.toString(),
                                "Pedido criado com sucesso",
                                List.of(convertToOrderResponseDTO(order)));
        }

        private ResponseDTO<OrderResponseDTO> buildErrorResponse(HttpStatus status, String message) {
                return new ResponseDTO<>(
                                status.toString(),
                                message,
                                null);
        }

        private OrderResponseDTO convertToOrderResponseDTO(Order order) {
                CustomerResponseDTO customerDTO = new CustomerResponseDTO(
                                order.getCustomer().getId(),
                                order.getCustomer().getFullName(),
                                order.getCustomer().getCpf(),
                                order.getCustomer().getBirthDate(),
                                order.getCustomer().getPhone(),
                                order.getCustomer().getGender(),
                                order.getCustomer().getAddress().stream().map(cc -> new AddressResponseDTO(
                                                cc.getId(),
                                                cc.getTitle(),
                                                cc.getCep(),
                                                cc.getResidenceType(),
                                                cc.getAddressType(),
                                                cc.getCategories().stream().map(AddressCategory::getCategory)
                                                                .collect(Collectors.toSet()),
                                                cc.getStreetName(),
                                                cc.getAddressNumber(),
                                                cc.getNeighborhoods(),
                                                cc.getCity(),
                                                cc.getState(),
                                                cc.getCountry(),
                                                cc.getObservations())).collect(Collectors.toSet()),
                                order.getCustomer().getCreditCardList().stream()
                                                .map(cc -> new CreditCardResponseDTO(
                                                                cc.getId(),
                                                                cc.getCardNumber(),
                                                                cc.getCardName(),
                                                                cc.getCardCode(),
                                                                cc.getCardFlag(),
                                                                cc.isMainCard()))
                                                .toList());

                AddressRequestDTO addressDTO = new AddressRequestDTO(
                                order.getAddress().getId().toString(),
                                order.getAddress().getTitle(),
                                order.getAddress().getCep(),
                                order.getAddress().getResidenceType(),
                                order.getAddress().getAddressType(),
                                order.getAddress().getCategories().stream().map(AddressCategory::getCategory)
                                                .collect(Collectors.toList()),
                                order.getAddress().getStreetName(),
                                order.getAddress().getAddressNumber(),
                                order.getAddress().getNeighborhoods(),
                                order.getAddress().getCity(),
                                order.getAddress().getState(),
                                order.getAddress().getCountry(),
                                order.getAddress().getObservations());

                OrderStatusResponseDTO statusDTO = new OrderStatusResponseDTO(
                                order.getStatus().getId(),
                                order.getStatus().getName());

                Set<OrderProductResponseDTO> orderProductsDTO = order.getOrderProducts().stream()
                                .map(orderProduct -> {
                                        Optional<Stock> productStock = stockRepository
                                                        .findByProductId(orderProduct.getProduct().getId());

                                        return productStock.map(stock -> new OrderProductResponseDTO(
                                                        orderProduct.getId(),
                                                        orderProduct.getQuantity(),
                                                        new ProductResponseDTO(
                                                                        orderProduct.getProduct().getId(),
                                                                        orderProduct.getProduct().getName(),
                                                                        orderProduct.getProduct().getDescription(),
                                                                        orderProduct.getProduct().getPrice(),
                                                                        orderProduct.getProduct().getSalePrice(),
                                                                        orderProduct.getProduct().getColor(),
                                                                        convertByteToBase64String(orderProduct
                                                                                        .getProduct().getImage()),
                                                                        orderProduct.getProduct().getIsActive(),
                                                                        new ProductCategoryResponseDTO(
                                                                                        orderProduct.getProduct()
                                                                                                        .getCategory()
                                                                                                        .getId(),
                                                                                        orderProduct.getProduct()
                                                                                                        .getCategory()
                                                                                                        .getName()),
                                                                        new PricingGroupResponseDTO(
                                                                                        orderProduct.getProduct()
                                                                                                        .getPricingGroup()
                                                                                                        .getId(),
                                                                                        orderProduct.getProduct()
                                                                                                        .getPricingGroup()
                                                                                                        .getName(),
                                                                                        orderProduct.getProduct()
                                                                                                        .getPricingGroup()
                                                                                                        .getProfitMargin()),
                                                                        orderProduct.getProduct().getType(),
                                                                        stock.getQuantity(),
                                                                        orderProduct.getProduct().getWeightKg())))
                                                        .orElse(null);

                                })
                                .collect(Collectors.toSet());

                return new OrderResponseDTO(
                                order.getId(),
                                customerDTO,
                                addressDTO,
                                statusDTO,
                                order.getType(),
                                orderProductsDTO,
                                order.getOrderPayments().stream()
                                                .map(orderPayment -> new OrderPaymentResponseDTO(
                                                                orderPayment.getId(),
                                                                orderPayment.getInstallments(),
                                                                orderPayment.getInstallmentValue(),
                                                                orderPayment.getMethod().toString(),
                                                                new CreditCardResponseDTO(
                                                                                orderPayment.getCreditCard().getId(),
                                                                                orderPayment.getCreditCard()
                                                                                                .getCardNumber(),
                                                                                orderPayment.getCreditCard()
                                                                                                .getCardName(),
                                                                                orderPayment.getCreditCard()
                                                                                                .getCardCode(),
                                                                                orderPayment.getCreditCard()
                                                                                                .getCardFlag(),
                                                                                orderPayment.getCreditCard()
                                                                                                .isMainCard())

                                                ))
                                                .collect(Collectors.toSet()),
                                // Carrega orderCoupons de forma lazy (dentro da transação)
                                // Como não podemos fazer FETCH de múltiplas coleções, carregamos aqui
                                order.getOrderCoupons() != null && !order.getOrderCoupons().isEmpty()
                                                ? order.getOrderCoupons().stream()
                                                                .map(orderCoupon -> new OrderCouponResponseDTO(
                                                                                orderCoupon.getId(),
                                                                                orderCoupon.getCoupon().getCode(),
                                                                                orderCoupon.getCoupon().getCouponType(),
                                                                                orderCoupon.getAmountUsed()))
                                                                .collect(Collectors.toList())
                                                : List.of(),
                                new OrderShipmentResponseDTO(
                                                order.getOrderShipment().getId(),
                                                order.getOrderShipment().getName(),
                                                order.getOrderShipment().getDeliveryTime(),
                                                order.getOrderShipment().getPrice()),
                                order.getTotalPrice(),
                                order.getCreatedAt(),
                                order.getUpdatedAt());
        }

        private String convertByteToBase64String(byte[] image) {
                if (image == null || image.length == 0) {
                        return null;
                }

                Tika tika = new Tika();
                String base64Image = Base64.getEncoder().encodeToString(image);
                String type = tika.detect(image);

                return "data:" + type + ";base64," + base64Image;
        }

        private byte[] convertBase64StringToByte(String base64String) {
                String[] parts = base64String.split(",");
                String base64Data = parts[1];
                return Base64.getDecoder().decode(base64Data);
        }

        /**
         * Atualiza o status de um pedido e gerencia o estoque conforme necessário
         */
        public ResponseDTO<OrderResponseDTO> updateOrderStatus(OrderStatusUpdateDTO statusUpdateDTO) {
                Order order = orderRepository.findById(statusUpdateDTO.orderId())
                                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

                OrderStatus newStatus = orderStatusRepository.findByName(statusUpdateDTO.statusName())
                                .orElseThrow(() -> new EntityNotFoundException("Status não encontrado"));

                String currentStatusName = order.getStatus().getName();
                String newStatusName = newStatus.getName();

                // Validações de transição de status
                validateStatusTransition(currentStatusName, newStatusName);

                // Atualiza o status
                order.setStatus(newStatus);
                Order updatedOrder = orderRepository.save(order);

                // Gerencia estoque baseado no novo status
                manageStockByStatus(order, currentStatusName, newStatusName);

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                String.format("Status do pedido atualizado para: %s", newStatusName),
                                List.of(convertToOrderResponseDTO(updatedOrder)));
        }

        private void validateStatusTransition(String currentStatus, String newStatus) {
                // Validações de transições permitidas
                if ("EM PROCESSAMENTO".equals(currentStatus)) {
                        if (!"APROVADO".equals(newStatus) && !"REPROVADO".equals(newStatus)
                                        && !"CANCELADO".equals(newStatus)) {
                                throw new BusinessException(
                                                String.format("Não é possível alterar status de %s para %s",
                                                                currentStatus, newStatus));
                        }
                } else if ("APROVADO".equals(currentStatus)) {
                        if (!"EM TRANSPORTE".equals(newStatus) && !"CANCELADO".equals(newStatus)) {
                                throw new BusinessException(
                                                String.format("Não é possível alterar status de %s para %s",
                                                                currentStatus, newStatus));
                        }
                } else if ("EM TRANSPORTE".equals(currentStatus)) {
                        if (!"ENTREGUE".equals(newStatus)) {
                                throw new BusinessException(
                                                String.format("Não é possível alterar status de %s para %s",
                                                                currentStatus, newStatus));
                        }
                } else if ("ENTREGUE".equals(currentStatus)) {
                        // Permite apenas trocas/devoluções
                        if (!newStatus.contains("TROCA") && !newStatus.contains("DEVOLUÇÃO")) {
                                throw new BusinessException(
                                                String.format("Não é possível alterar status de %s para %s",
                                                                currentStatus, newStatus));
                        }
                }
        }

        private void manageStockByStatus(Order order, String oldStatus, String newStatus) {
                // Se mudou para APROVADO, faz a baixa efetiva do estoque
                if ("APROVADO".equals(newStatus) && !"APROVADO".equals(oldStatus)) {
                        performStockDeduction(order);
                }

                // Se mudou para REPROVADO ou CANCELADO, desbloqueia o estoque
                if (("REPROVADO".equals(newStatus) || "CANCELADO".equals(newStatus))
                                && !"REPROVADO".equals(oldStatus) && !"CANCELADO".equals(oldStatus)) {
                        releaseStockReservation(order);
                }
        }

        /**
         * Realiza a baixa efetiva do estoque quando pedido é aprovado
         */
        private void performStockDeduction(Order order) {
                for (OrderProduct orderProduct : order.getOrderProducts()) {
                        Stock stock = stockRepository.findByProductId(orderProduct.getProduct().getId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Estoque do produto " + orderProduct.getProduct().getName()
                                                                        + " não encontrado"));

                        // Baixa efetiva: reduz quantidade e remove da reserva
                        stock.setQuantity(stock.getQuantity() - orderProduct.getQuantity());
                        stock.setReservedQuantity(stock.getReservedQuantity() - orderProduct.getQuantity());
                        stockRepository.save(stock);
                }
        }

        /**
         * Libera a reserva de estoque quando pedido é reprovado ou cancelado
         */
        private void releaseStockReservation(Order order) {
                for (OrderProduct orderProduct : order.getOrderProducts()) {
                        Stock stock = stockRepository.findByProductId(orderProduct.getProduct().getId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Estoque do produto " + orderProduct.getProduct().getName()
                                                                        + " não encontrado"));

                        // Remove apenas da reserva, mantém a quantidade
                        stock.setReservedQuantity(stock.getReservedQuantity() - orderProduct.getQuantity());
                        stockRepository.save(stock);
                }
        }
}

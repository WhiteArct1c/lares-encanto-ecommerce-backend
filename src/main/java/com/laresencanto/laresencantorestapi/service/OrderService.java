package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.address.Address;
import com.laresencanto.laresencantorestapi.domain.creditCard.CreditCard;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.order.*;
import com.laresencanto.laresencantorestapi.domain.product.Product;
import com.laresencanto.laresencantorestapi.domain.product.Stock;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class OrderService {

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

        public OrderService(
                        OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
                        StockRepository stockRepository,
                        CustomerRepository customerRepository, AddressRepository addressRepository,
                        CreditCardRepository creditCardRepository, ProductRepository productRepository,
                        OrderPaymentRepository orderPaymentRepository, OrderShipmentRepository orderShipmentRepository,
                        ShippingService shippingService) {
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
        }

        public ResponseDTO<OrderResponseDTO> listAllOrders() {
                List<OrderResponseDTO> orders = orderRepository.findAll()
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos encontrados com sucesso",
                                orders);

        }

        public ResponseDTO<OrderResponseDTO> listPendingOrders() {
                List<OrderResponseDTO> orders = orderRepository.findAllByStatusName("EM PROCESSAMENTO")
                                .stream().map(this::convertToOrderResponseDTO).toList();

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Pedidos encontrados com sucesso",
                                orders);
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
                // 1. Validação e obtenção do cliente
                Customer customer = getAuthenticatedCustomer();

                // 2. Validações antes de criar o pedido
                validatePayments(requestDTO.orderPayments(), requestDTO.totalPrice());
                validateProductsStock(requestDTO.orderProducts());

                // 3. Construção do pedido
                Order order = buildOrder(requestDTO, customer);

                // 4. Persistência e retorno
                try {
                        Order savedOrder = orderRepository.save(order);
                        return buildSuccessResponse(savedOrder);
                } catch (BusinessException e) {
                        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
                } catch (Exception e) {
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

                Order savedOrder = orderRepository.save(order);

                // Configuração dos pagamentos
                order.setOrderPayments(
                                buildOrderPayments(customer, savedOrder, requestDTO.orderPayments().stream().toList()));

                // Configuração dos items do pedido
                order.setOrderProducts(buildOrderProducts(savedOrder, requestDTO.orderProducts().stream().toList()));

                // Configuração do envio (calcula automaticamente baseado nos produtos e
                // endereço)
                order.setOrderShipment(buildOrderShipment(savedOrder, requestDTO.shipping(), requestDTO.address(),
                                requestDTO.orderProducts()));

                return order;
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

        private void validatePayments(Set<OrderPaymentResponseDTO> payments, Double totalPrice) {
                if (payments == null || payments.isEmpty()) {
                        throw new BusinessException("É necessário informar pelo menos uma forma de pagamento");
                }

                BigDecimal totalPayments = payments.stream()
                                .map(p -> p.installmentValue().multiply(BigDecimal.valueOf(p.installments())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalPriceBD = BigDecimal.valueOf(totalPrice);

                // Validar se soma dos pagamentos confere com o total
                if (totalPayments.compareTo(totalPriceBD) != 0) {
                        throw new BusinessException(
                                        String.format("A soma dos pagamentos (R$ %.2f) não confere com o valor total do pedido (R$ %.2f)",
                                                        totalPayments.doubleValue(), totalPrice));
                }

                // Validar valor mínimo por cartão (R$ 10,00)
                BigDecimal minimumValue = new BigDecimal("10.00");
                for (OrderPaymentResponseDTO payment : payments) {
                        if (payment.creditCard() != null) {
                                BigDecimal paymentTotal = payment.installmentValue()
                                                .multiply(BigDecimal.valueOf(payment.installments()));

                                if (paymentTotal.compareTo(minimumValue) < 0) {
                                        throw new BusinessException(
                                                        String.format("O valor mínimo por cartão de crédito é R$ 10,00. Valor informado: R$ %.2f",
                                                                        paymentTotal.doubleValue()));
                                }
                        }
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

        private OrderPayment buildOrderPayment(Customer customer, Order order, OrderPaymentResponseDTO paymentDTO) {
                List<CreditCard> customerCreditCards = creditCardRepository.findByCustomerId(customer.getId());

                CreditCard orderCreditCard = customerCreditCards.stream()
                                .filter(cc -> Objects.equals(cc.getId(), paymentDTO.creditCard().id()))
                                .findFirst()
                                .orElse(new CreditCard());

                if (orderCreditCard.getId() == null) {
                        orderCreditCard.setCardNumber(paymentDTO.creditCard().cardNumber());
                        orderCreditCard.setCardName(paymentDTO.creditCard().cardName());
                        orderCreditCard.setCardCode(paymentDTO.creditCard().cardCode());
                        orderCreditCard.setCardFlag(paymentDTO.creditCard().cardFlag());
                        orderCreditCard.setMainCard(paymentDTO.creditCard().mainCard());
                        orderCreditCard.setCustomer(customer);
                        creditCardRepository.save(orderCreditCard);
                }

                OrderPayment payment = new OrderPayment();
                payment.setInstallments(paymentDTO.installments());
                payment.setInstallmentValue(paymentDTO.installmentValue());
                payment.setMethod(OrderPayment.PaymentMethod.valueOf(paymentDTO.paymentMethod()));
                payment.setCreditCard(orderCreditCard);
                payment.setOrder(order);

                return orderPaymentRepository.save(payment);
        }

        private Set<OrderProduct> buildOrderProducts(Order order, List<OrderProductResponseDTO> productDTOs) {
                return productDTOs.stream()
                                .map(productDTO -> buildOrderProduct(order, productDTO))
                                .collect(Collectors.toSet());
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

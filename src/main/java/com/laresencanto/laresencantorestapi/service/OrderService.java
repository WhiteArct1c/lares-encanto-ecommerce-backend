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
import com.laresencanto.laresencantorestapi.exception.EntityNotFoundException;
import com.laresencanto.laresencantorestapi.repository.*;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public OrderService(
            OrderRepository orderRepository
            , OrderStatusRepository orderStatusRepository
            , StockRepository stockRepository,
            CustomerRepository customerRepository, AddressRepository addressRepository, CreditCardRepository creditCardRepository, ProductRepository productRepository, OrderPaymentRepository orderPaymentRepository, OrderShipmentRepository orderShipmentRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.stockRepository = stockRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.creditCardRepository = creditCardRepository;
        this.productRepository = productRepository;
        this.orderPaymentRepository = orderPaymentRepository;
        this.orderShipmentRepository = orderShipmentRepository;
    }

    public ResponseDTO<OrderResponseDTO> listAllOrders() {
        List<OrderResponseDTO> orders = orderRepository.findAll()
                .stream().map(this::convertToOrderResponseDTO).toList();

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Pedidos encontrados com sucesso",
                orders
        );

    }

    public ResponseDTO<OrderResponseDTO> listPendingOrders() {
        List<OrderResponseDTO> orders = orderRepository.findAllByStatusName("EM PROCESSAMENTO")
                .stream().map(this::convertToOrderResponseDTO).toList();

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Pedidos encontrados com sucesso",
                orders
        );
    }

    public ResponseDTO<OrderResponseDTO> listCustomerOrders() {
        Customer customer = getAuthenticatedCustomer();

        List<OrderResponseDTO> orders = orderRepository.findAllByCustomerId(customer.getId())
                .stream().map(this::convertToOrderResponseDTO).toList();

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Pedidos encontrados com sucesso",
                orders
        );
    }

    public ResponseDTO<OrderResponseDTO> createOrder(OrderCreateRequestDTO requestDTO) {
        // 1. Validação e obtenção do cliente
        Customer customer = getAuthenticatedCustomer();

        // 2. Construção do pedido
        Order order = buildOrder(requestDTO, customer);

        // 3. Persistência e retorno
        try{
            Order savedOrder = orderRepository.save(order);
            return buildSuccessResponse(savedOrder);
        }catch (Exception e){
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Erro ao criar o pedido");
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
        order.setOrderPayments(buildOrderPayments(customer, savedOrder, requestDTO.orderPayments().stream().toList()));

        // Configuração dos items do pedido
        order.setOrderProducts(buildOrderProducts(savedOrder, requestDTO.orderProducts().stream().toList()));

        // Configuração do envio
        order.setOrderShipment(buildOrderShipment(savedOrder, requestDTO.shipping()));

        return order;
    }

    private Address buildAddress(Customer customer, Order order, AddressRequestDTO addressDTO) {
        if(addressDTO.id() == null || addressDTO.id().isEmpty()){
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
        }else{
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

    private Set<OrderPayment> buildOrderPayments(Customer customer, Order order, List<OrderPaymentResponseDTO> paymentDTOs) {
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


        if(orderCreditCard.getId() == null) {
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

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(productDTO.quantity());
        orderProduct.setProduct(product);
        orderProduct.setOrder(order);

        stock.setReservedQuantity(productDTO.quantity()); //bloqueia os produtos

        return orderProduct;
    }

    private OrderShipment buildOrderShipment(Order order, OrderShipmentResponseDTO shipmentDTO) {
        OrderShipment shipment = new OrderShipment();
        shipment.setName(shipmentDTO.name());
        shipment.setDeliveryTime(shipmentDTO.deliveryTime());
        shipment.setPrice(shipmentDTO.price());
        shipment.setOrder(order);

        return orderShipmentRepository.save(shipment);
    }

    private OrderStatus getDefaultOrderStatus() {
        return orderStatusRepository.findByName("EM PROCESSAMENTO")
                .orElseThrow(() -> new EntityNotFoundException("Status padrão 'EM PROCESSAMENTO' não encontrado"));
    }

    private ResponseDTO<OrderResponseDTO> buildSuccessResponse(Order order) {
        return new ResponseDTO<>(
                HttpStatus.CREATED.toString(),
                "Pedido criado com sucesso",
                List.of(convertToOrderResponseDTO(order))
        );
    }

    private ResponseDTO<OrderResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseDTO<>(
                status.toString(),
                message,
                null
        );
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
                        cc.getCategories().stream().map(AddressCategory::getCategory).collect(Collectors.toSet()),
                        cc.getStreetName(),
                        cc.getAddressNumber(),
                        cc.getNeighborhoods(),
                        cc.getCity(),
                        cc.getState(),
                        cc.getCountry(),
                        cc.getObservations()
                )).collect(Collectors.toSet()),
                order.getCustomer().getCreditCardList().stream()
                        .map(cc -> new CreditCardResponseDTO(
                                cc.getId(),
                                cc.getCardNumber(),
                                cc.getCardName(),
                                cc.getCardCode(),
                                cc.getCardFlag(),
                                cc.isMainCard()
                        ))
                        .toList()
        );

        AddressRequestDTO addressDTO = new AddressRequestDTO(
                order.getAddress().getId().toString(),
                order.getAddress().getTitle(),
                order.getAddress().getCep(),
                order.getAddress().getResidenceType(),
                order.getAddress().getAddressType(),
                order.getAddress().getCategories().stream().map(AddressCategory::getCategory).collect(Collectors.toList()),
                order.getAddress().getStreetName(),
                order.getAddress().getAddressNumber(),
                order.getAddress().getNeighborhoods(),
                order.getAddress().getCity(),
                order.getAddress().getState(),
                order.getAddress().getCountry(),
                order.getAddress().getObservations()
        );

        OrderStatusResponseDTO statusDTO = new OrderStatusResponseDTO(
                order.getStatus().getId(),
                order.getStatus().getName()
        );

        Set<OrderProductResponseDTO> orderProductsDTO = order.getOrderProducts().stream()
                .map(orderProduct -> {
                    Optional<Stock> productStock = stockRepository.findByProductId(orderProduct.getProduct().getId());

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
                                    convertByteToBase64String(orderProduct.getProduct().getImage()),
                                    orderProduct.getProduct().getIsActive(),
                                    new ProductCategoryResponseDTO(
                                            orderProduct.getProduct().getCategory().getId(),
                                            orderProduct.getProduct().getCategory().getName()
                                    ),
                                    new PricingGroupResponseDTO(
                                            orderProduct.getProduct().getPricingGroup().getId(),
                                            orderProduct.getProduct().getPricingGroup().getName(),
                                            orderProduct.getProduct().getPricingGroup().getProfitMargin()
                                    ),
                                    orderProduct.getProduct().getType(),
                                    stock.getQuantity()
                            )
                    )).orElse(null);

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
                                        orderPayment.getCreditCard().getCardNumber(),
                                        orderPayment.getCreditCard().getCardName(),
                                        orderPayment.getCreditCard().getCardCode(),
                                        orderPayment.getCreditCard().getCardFlag(),
                                        orderPayment.getCreditCard().isMainCard()
                                )

                        ))
                        .collect(Collectors.toSet()),
                new OrderShipmentResponseDTO(
                        order.getOrderShipment().getId(),
                        order.getOrderShipment().getName(),
                        order.getOrderShipment().getDeliveryTime(),
                        order.getOrderShipment().getPrice()
                ),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private String convertByteToBase64String(byte[] image) {
        Tika tika = new Tika();

        String base64Image = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
        String type = tika.detect(image);

        return "data:" + type + ";base64," + base64Image;
    }

    private byte[] convertBase64StringToByte(String base64String) {
        String[] parts = base64String.split(",");
        String base64Data = parts[1];
        return Base64.getDecoder().decode(base64Data);
    }
}

package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.coupon.Coupon;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.exchange.Exchange;
import com.laresencanto.laresencantorestapi.domain.exchange.ExchangeItem;
import com.laresencanto.laresencantorestapi.domain.order.Order;
import com.laresencanto.laresencantorestapi.domain.order.OrderProduct;
import com.laresencanto.laresencantorestapi.domain.order.OrderStatus;
import com.laresencanto.laresencantorestapi.domain.product.Product;
import com.laresencanto.laresencantorestapi.domain.product.Stock;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeItemDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeStatusUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ColorResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.TagResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;
import org.apache.tika.Tika;

import java.util.Base64;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeAuthorizationDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeConfirmationDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeStatusUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.exchange.ExchangeItemResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.exchange.ExchangeResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderProductResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderStatusResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ProductResponseDTO;
import com.laresencanto.laresencantorestapi.exception.BusinessException;
import com.laresencanto.laresencantorestapi.exception.EntityNotFoundException;
import com.laresencanto.laresencantorestapi.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

  private final ExchangeRepository exchangeRepository;
  private final ExchangeItemRepository exchangeItemRepository;
  private final CouponRepository couponRepository;
  private final OrderRepository orderRepository;
  private final OrderProductRepository orderProductRepository;
  private final OrderStatusRepository orderStatusRepository;
  private final StockRepository stockRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final StockRepository stockRepositoryForProduct;

  public ExchangeService(
      ExchangeRepository exchangeRepository,
      ExchangeItemRepository exchangeItemRepository,
      CouponRepository couponRepository,
      OrderRepository orderRepository,
      OrderProductRepository orderProductRepository,
      OrderStatusRepository orderStatusRepository,
      StockRepository stockRepository,
      CustomerRepository customerRepository,
      ProductRepository productRepository) {
    this.exchangeRepository = exchangeRepository;
    this.exchangeItemRepository = exchangeItemRepository;
    this.couponRepository = couponRepository;
    this.orderRepository = orderRepository;
    this.orderProductRepository = orderProductRepository;
    this.orderStatusRepository = orderStatusRepository;
    this.stockRepository = stockRepository;
    this.customerRepository = customerRepository;
    this.productRepository = productRepository;
    this.stockRepositoryForProduct = stockRepository;
  }

  /**
   * RF0040 - Solicitar troca/devolução de um ou mais produtos
   * RN0043 - Valida que pedido está ENTREGUE
   * 
   * Cria UMA Exchange com múltiplos itens (ExchangeItems)
   * O pedido permanece ENTREGUE mesmo em caso de devolução total
   */
  @Transactional
  public ResponseDTO<ExchangeResponseDTO> requestExchange(ExchangeRequestDTO requestDTO) {
    Customer customer = getAuthenticatedCustomer();

    // Busca o pedido
    Order order = orderRepository.findById(requestDTO.orderId())
        .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

    // RN0043 - Valida que pedido está ENTREGUE
    if (!"ENTREGUE".equals(order.getStatus().getName())) {
      throw new BusinessException(
          "Apenas pedidos com status ENTREGUE podem ter itens trocados ou devolvidos");
    }

    // Valida que o pedido pertence ao cliente
    if (!order.getCustomer().getId().equals(customer.getId())) {
      throw new BusinessException("Este pedido não pertence ao cliente autenticado");
    }

    // Valida que há pelo menos um item
    if (requestDTO.items() == null || requestDTO.items().isEmpty()) {
      throw new BusinessException("Pelo menos um item deve ser informado para troca/devolução");
    }

    // Valida que não há orderProductId duplicados
    long distinctOrderProductIds = requestDTO.items().stream()
        .map(ExchangeItemDTO::orderProductId)
        .distinct()
        .count();

    if (distinctOrderProductIds < requestDTO.items().size()) {
      throw new BusinessException("Não é permitido informar o mesmo produto mais de uma vez na mesma solicitação");
    }

    // Busca status "TROCA SOLICITADA"
    OrderStatus status = orderStatusRepository.findByName("TROCA SOLICITADA")
        .orElseThrow(() -> new EntityNotFoundException("Status 'TROCA SOLICITADA' não encontrado"));

    // Busca todas as trocas existentes do pedido para validação
    List<Exchange> existingExchanges = exchangeRepository.findAllByOrderId(order.getId());

    // Cria UMA Exchange para toda a solicitação
    Exchange exchange = new Exchange();
    exchange.setOrder(order);
    exchange.setStatus(status);
    exchange.setReturnToStock(false); // Será definido na confirmação
    exchange.setCouponGenerated(false);
    exchange.setItems(new ArrayList<>());

    Exchange savedExchange = exchangeRepository.save(exchange);

    // Processa cada item da requisição e cria ExchangeItems
    for (ExchangeItemDTO item : requestDTO.items()) {
      // Busca o produto do pedido
      OrderProduct orderProduct = orderProductRepository.findById(item.orderProductId())
          .orElseThrow(() -> new EntityNotFoundException(
              String.format("Produto do pedido com ID %d não encontrado", item.orderProductId())));

      // Valida que o produto pertence ao pedido
      if (!orderProduct.getOrder().getId().equals(order.getId())) {
        throw new BusinessException(
            String.format("O produto com ID %d não pertence ao pedido especificado", item.orderProductId()));
      }

      // Valida quantidade
      if (item.quantity() > orderProduct.getQuantity()) {
        throw new BusinessException(
            String.format("Quantidade solicitada (%d) para o produto %s é maior que a quantidade comprada (%d)",
                item.quantity(), orderProduct.getProduct().getName(), orderProduct.getQuantity()));
      }

      // Verifica se já existe uma troca para este produto (soma quantidades de todos
      // os ExchangeItems)
      int totalExchangedQuantity = existingExchanges.stream()
          .flatMap(e -> e.getItems().stream())
          .filter(ei -> ei.getOrderProduct().getId().equals(orderProduct.getId()))
          .mapToInt(ExchangeItem::getQuantity)
          .sum();

      // Soma também as quantidades que estão sendo solicitadas agora (para o mesmo produto)
      int currentRequestQuantity = requestDTO.items().stream()
          .filter(i -> i.orderProductId().equals(item.orderProductId()))
          .mapToInt(ExchangeItemDTO::quantity)
          .sum();

      if (totalExchangedQuantity + currentRequestQuantity > orderProduct.getQuantity()) {
        throw new BusinessException(
            String.format(
                "A quantidade total de trocas/devoluções (%d) para o produto %s excede a quantidade comprada (%d)",
                totalExchangedQuantity + currentRequestQuantity, orderProduct.getProduct().getName(),
                orderProduct.getQuantity()));
      }

      // Cria o ExchangeItem
      ExchangeItem exchangeItem = new ExchangeItem();
      exchangeItem.setExchange(savedExchange);
      exchangeItem.setOrderProduct(orderProduct);
      exchangeItem.setQuantity(item.quantity());
      exchangeItem.setReason(item.reason());

      exchangeItemRepository.save(exchangeItem);
      savedExchange.getItems().add(exchangeItem);
    }

    // Salva a Exchange atualizada com os itens
    Exchange finalExchange = exchangeRepository.save(savedExchange);

    // IMPORTANTE: O pedido permanece ENTREGUE mesmo em caso de devolução total
    // Não alteramos o status do pedido

    return new ResponseDTO<>(
        HttpStatus.CREATED.toString(),
        String.format("Troca/devolução de %d item(ns) solicitada com sucesso", finalExchange.getItems().size()),
        List.of(convertToDTO(finalExchange)));
  }

  /**
   * RF0042 - Visualizar todas as trocas pendentes (admin)
   */
  @Transactional(readOnly = true)
  public ResponseDTO<ExchangeResponseDTO> listPendingExchanges() {
    List<Exchange> exchanges = exchangeRepository.findAllByStatusName("TROCA SOLICITADA");

    List<ExchangeResponseDTO> exchangesDTO = exchanges.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        "Trocas pendentes encontradas com sucesso",
        exchangesDTO);
  }

  /**
   * RF0041 - Autorizar ou recusar troca (admin)
   * RNF0046 - Notificação (mockada por enquanto)
   */
  @Transactional
  public ResponseDTO<ExchangeResponseDTO> authorizeExchange(ExchangeAuthorizationDTO requestDTO) {
    Exchange exchange = exchangeRepository.findById(requestDTO.exchangeId())
        .orElseThrow(() -> new EntityNotFoundException("Troca não encontrada"));

    if (!"TROCA SOLICITADA".equals(exchange.getStatus().getName())) {
      throw new BusinessException(
          String.format("Apenas trocas com status TROCA SOLICITADA podem ser autorizadas. Status atual: %s",
              exchange.getStatus().getName()));
    }

    OrderStatus newStatus;
    String message;

    if ("APPROVE".equalsIgnoreCase(requestDTO.action())) {
      // RF0041 - Status TROCA ACEITA (equivalente a TROCA AUTORIZADA)
      newStatus = orderStatusRepository.findByName("TROCA ACEITA")
          .orElseThrow(() -> new EntityNotFoundException("Status 'TROCA ACEITA' não encontrado"));
      message = "Troca autorizada com sucesso";

      // RNF0046 - Notificação (mockada - pode ser implementada depois com sistema de
      // notificações)
      // TODO: Implementar notificação ao cliente

    } else if ("REJECT".equalsIgnoreCase(requestDTO.action())) {
      newStatus = orderStatusRepository.findByName("TROCA RECUSADA")
          .orElseThrow(() -> new EntityNotFoundException("Status 'TROCA RECUSADA' não encontrado"));
      message = "Troca recusada";
    } else {
      throw new BusinessException("Ação inválida. Use 'APPROVE' ou 'REJECT'");
    }

    exchange.setStatus(newStatus);
    Exchange updatedExchange = exchangeRepository.save(exchange);

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        message,
        List.of(convertToDTO(updatedExchange)));
  }

  /**
   * RF0043 - Confirmar recebimento de itens para troca (admin)
   * RF0044 - Gerar cupom de troca após recebimento (UM cupom somando todos os
   * itens)
   * RN0042 - Alterar status para TROCA CONCLUÍDA
   * RF0054 - Reentrada em estoque (se returnToStock = true) para todos os itens
   */
  @Transactional
  public ResponseDTO<ExchangeResponseDTO> confirmExchangeReceipt(ExchangeConfirmationDTO requestDTO) {
    Exchange exchange = exchangeRepository.findById(requestDTO.exchangeId())
        .orElseThrow(() -> new EntityNotFoundException("Troca não encontrada"));

    if (!"TROCA ACEITA".equals(exchange.getStatus().getName())) {
      throw new BusinessException(
          String.format("Apenas trocas com status TROCA ACEITA podem ser confirmadas. Status atual: %s",
              exchange.getStatus().getName()));
    }

    // Atualiza se deve retornar ao estoque
    exchange.setReturnToStock(requestDTO.returnToStock());

    // RF0054 - Reentrada em estoque (se solicitado) - processa todos os itens
    if (requestDTO.returnToStock()) {
      performStockReentry(exchange);
    }

    // RN0042 - Altera status para TROCA CONCLUÍDA
    OrderStatus completedStatus = orderStatusRepository.findByName("TROCA CONCLUÍDA")
        .orElseThrow(() -> new EntityNotFoundException("Status 'TROCA CONCLUÍDA' não encontrado"));
    exchange.setStatus(completedStatus);

    // RF0044 - Gera UM cupom de troca somando o valor de TODOS os itens
    if (!exchange.getCouponGenerated()) {
      generateExchangeCoupon(exchange);
      exchange.setCouponGenerated(true);
    }

    Exchange updatedExchange = exchangeRepository.save(exchange);

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        "Recebimento confirmado e cupom gerado com sucesso",
        List.of(convertToDTO(updatedExchange)));
  }

  /**
   * Lista todas as trocas do cliente autenticado
   */
  @Transactional(readOnly = true)
  public ResponseDTO<ExchangeResponseDTO> listCustomerExchanges() {
    Customer customer = getAuthenticatedCustomer();
    List<Exchange> exchanges = exchangeRepository.findAllByCustomerId(customer.getId());

    List<ExchangeResponseDTO> exchangesDTO = exchanges.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        "Trocas encontradas com sucesso",
        exchangesDTO);
  }

  /**
   * Lista todas as trocas (apenas admin)
   */
  @Transactional(readOnly = true)
  public ResponseDTO<ExchangeResponseDTO> listAllExchanges() {
    // Valida que é admin
    if (!isAdminUser()) {
      throw new BusinessException("Apenas administradores podem listar todas as trocas");
    }

    List<Exchange> exchanges = exchangeRepository.findAllWithRelations();

    List<ExchangeResponseDTO> exchangesDTO = exchanges.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        "Todas as trocas encontradas com sucesso",
        exchangesDTO);
  }

  /**
   * Lista trocas de um cliente específico
   * - Admin pode listar qualquer cliente
   * - Cliente só pode listar suas próprias trocas
   */
  @Transactional(readOnly = true)
  public ResponseDTO<ExchangeResponseDTO> listExchangesByCustomerId(Long customerId) {
    Customer authenticatedCustomer = getAuthenticatedCustomer();
    boolean isAdmin = isAdminUser();

    // Se não for admin, só pode ver suas próprias trocas
    if (!isAdmin && !authenticatedCustomer.getId().equals(customerId)) {
      throw new BusinessException("Você só pode visualizar suas próprias trocas");
    }

    List<Exchange> exchanges = exchangeRepository.findAllByCustomerId(customerId);

    List<ExchangeResponseDTO> exchangesDTO = exchanges.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        String.format("Trocas do cliente %d encontradas com sucesso", customerId),
        exchangesDTO);
  }

  /**
   * Verifica se o usuário autenticado é admin
   */
  private boolean isAdminUser() {
    Customer customer = getAuthenticatedCustomer();
    return customer.getUser() != null &&
        customer.getUser().getRole() != null &&
        customer.getUser().getRole().name().equals("ADMIN");
  }

  /**
   * Atualiza o status de uma troca diretamente (admin)
   */
  @Transactional
  public ResponseDTO<ExchangeResponseDTO> updateExchangeStatus(
      ExchangeStatusUpdateDTO requestDTO) {
    Exchange exchange = exchangeRepository.findById(requestDTO.exchangeId())
        .orElseThrow(() -> new EntityNotFoundException("Troca não encontrada"));

    OrderStatus newStatus = orderStatusRepository.findByName(requestDTO.statusName())
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Status '%s' não encontrado", requestDTO.statusName())));

    // Valida transição de status (opcional - pode ser mais flexível)
    String currentStatusName = exchange.getStatus().getName();
    validateStatusTransition(currentStatusName, requestDTO.statusName());

    exchange.setStatus(newStatus);
    Exchange updatedExchange = exchangeRepository.save(exchange);

    return new ResponseDTO<>(
        HttpStatus.OK.toString(),
        String.format("Status da troca atualizado para: %s", requestDTO.statusName()),
        List.of(convertToDTO(updatedExchange)));
  }

  /**
   * Valida transições de status permitidas
   */
  private void validateStatusTransition(String currentStatus, String newStatus) {
    // TROCA SOLICITADA pode ir para TROCA ACEITA ou TROCA RECUSADA
    if ("TROCA SOLICITADA".equals(currentStatus)) {
      if (!"TROCA ACEITA".equals(newStatus) && !"TROCA RECUSADA".equals(newStatus)) {
        throw new BusinessException(
            String.format("Não é possível alterar status de %s para %s", currentStatus, newStatus));
      }
    }
    // TROCA ACEITA pode ir para TROCA CONCLUÍDA
    else if ("TROCA ACEITA".equals(currentStatus)) {
      if (!"TROCA CONCLUÍDA".equals(newStatus)) {
        throw new BusinessException(
            String.format("Não é possível alterar status de %s para %s", currentStatus, newStatus));
      }
    }
    // TROCA RECUSADA e TROCA CONCLUÍDA são finais (não podem mudar)
    else if ("TROCA RECUSADA".equals(currentStatus) || "TROCA CONCLUÍDA".equals(currentStatus)) {
      throw new BusinessException(
          String.format("Status %s é final e não pode ser alterado", currentStatus));
    }
  }

  /**
   * RF0054 - Realizar reentrada em estoque
   * Processa todos os itens da Exchange
   */
  private void performStockReentry(Exchange exchange) {
    for (ExchangeItem item : exchange.getItems()) {
      OrderProduct orderProduct = item.getOrderProduct();
      Stock stock = stockRepository.findByProductId(orderProduct.getProduct().getId())
          .orElseThrow(() -> new EntityNotFoundException(
              "Estoque do produto " + orderProduct.getProduct().getName() + " não encontrado"));

      // Adiciona a quantidade de volta ao estoque
      stock.setQuantity(stock.getQuantity() + item.getQuantity());
      stockRepository.save(stock);
    }
  }

  /**
   * RF0044 - Gerar cupom de troca
   * RN0036 - Geração de cupom de troca com valor do crédito
   * 
   * IMPORTANTE:
   * - Gera UM único cupom somando o valor de TODOS os itens da Exchange
   * - Usa o preço unitário pago na compra (armazenado em OrderProduct.unitPrice),
   * não o preço atual do produto, para garantir que o cupom tenha o valor correto
   * mesmo se o preço do produto mudou desde a compra.
   */
  private Coupon generateExchangeCoupon(Exchange exchange) {
    // Calcula o valor total do cupom somando todos os itens
    BigDecimal totalCouponValue = BigDecimal.ZERO;

    for (ExchangeItem item : exchange.getItems()) {
      OrderProduct orderProduct = item.getOrderProduct();

      // Calcula o valor deste item baseado no preço unitário PAGO NA COMPRA
      BigDecimal unitPricePaid = orderProduct.getUnitPrice();
      if (unitPricePaid == null) {
        // Fallback para preço atual se unitPrice não estiver disponível (pedidos
        // antigos)
        unitPricePaid = orderProduct.getProduct().getSalePrice();
      }

      // Valor deste item = preço unitário × quantidade trocada
      BigDecimal itemValue = unitPricePaid.multiply(BigDecimal.valueOf(item.getQuantity()));
      totalCouponValue = totalCouponValue.add(itemValue);
    }

    // Gera código único do cupom
    String couponCode = generateCouponCode();

    // Busca o cliente do pedido
    Customer customer = exchange.getOrder().getCustomer();

    Coupon coupon = new Coupon();
    coupon.setCode(couponCode);
    coupon.setValue(totalCouponValue); // Valor total de todos os itens
    coupon.setUsedValue(BigDecimal.ZERO);
    coupon.setIsActive(true);
    coupon.setExpiresAt(LocalDateTime.now().plusMonths(6)); // Expira em 6 meses
    coupon.setCustomer(customer);
    coupon.setExchange(exchange);
    coupon.setCouponType("EXCHANGE");
    // Campos de controle de uso (não se aplicam a EXCHANGE, mas não podem ser nulos)
    coupon.setMaxUses(null);
    coupon.setUsedCount(0);

    return couponRepository.save(coupon);
  }

  /**
   * Gera código único para cupom
   */
  private String generateCouponCode() {
    String code;
    do {
      code = "TROCA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    } while (couponRepository.findByCode(code).isPresent());

    return code;
  }

  /**
   * Converte Exchange para DTO
   */
  private ExchangeResponseDTO convertToDTO(Exchange exchange) {
    // Converte todos os ExchangeItems para DTOs
    List<ExchangeItemResponseDTO> itemsDTO = exchange.getItems().stream()
        .map(item -> {
          OrderProduct orderProduct = item.getOrderProduct();
          Product product = orderProduct.getProduct();
          ProductResponseDTO productDTO = convertProductToDTO(product);

          OrderProductResponseDTO orderProductDTO = new OrderProductResponseDTO(
              orderProduct.getId(),
              orderProduct.getQuantity(),
              productDTO);

          return new ExchangeItemResponseDTO(
              item.getId(),
              orderProductDTO,
              item.getQuantity(),
              item.getReason());
        })
        .collect(Collectors.toList());

    OrderStatusResponseDTO statusDTO = new OrderStatusResponseDTO(
        exchange.getStatus().getId(),
        exchange.getStatus().getName());

    return new ExchangeResponseDTO(
        exchange.getId(),
        exchange.getOrder().getId(),
        itemsDTO,
        statusDTO,
        exchange.getReturnToStock(),
        exchange.getCouponGenerated(),
        exchange.getCreatedAt(),
        exchange.getUpdatedAt());
  }

  /**
   * Converte Product para ProductResponseDTO
   */
  private ProductResponseDTO convertProductToDTO(Product product) {
    Integer stockQuantity = stockRepositoryForProduct.findByProductId(product.getId())
        .map(Stock::getQuantity)
        .orElse(0);

    PricingGroupResponseDTO pricingGroup = new PricingGroupResponseDTO(
        product.getPricingGroup().getId(),
        product.getPricingGroup().getName(),
        product.getPricingGroup().getProfitMargin());

    ProductCategoryResponseDTO productCategory = new ProductCategoryResponseDTO(
        product.getCategory().getId(),
        product.getCategory().getName());

    List<ColorResponseDTO> colors = 
        product.getColors() != null
            ? product.getColors().stream()
                .map(c -> new ColorResponseDTO(
                    c.getId(), c.getHexCode(), c.getName()))
                .toList()
            : List.of();

    List<TagResponseDTO> tags = 
        product.getTags() != null
            ? product.getTags().stream()
                .map(t -> new TagResponseDTO(
                    t.getId(), t.getName()))
                .toList()
            : List.of();

    return new ProductResponseDTO(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getSalePrice(),
        product.getColor(),
        convertByteToBase64String(product.getImage()),
        product.getIsActive(),
        productCategory,
        pricingGroup,
        product.getType(),
        stockQuantity,
        product.getWeightKg(),
        colors,
        tags);
  }

  /**
   * Converte byte[] para string base64
   */
  private String convertByteToBase64String(byte[] image) {
    if (image == null || image.length == 0) {
      return null;
    }

    Tika tika = new Tika();
    String base64Image = Base64.getEncoder().encodeToString(image);
    String type = tika.detect(image);

    return "data:" + type + ";base64," + base64Image;
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

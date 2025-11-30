package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.shipping.ShippingOption;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.shipping.ShippingCalculationRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderProductResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.shipping.ShippingOptionResponseDTO;
import com.laresencanto.laresencantorestapi.repository.ShippingOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShippingService {

  private final ShippingOptionRepository shippingOptionRepository;

  // Peso médio padrão por produto (em kg) - usado apenas se o produto não tiver
  // peso definido
  private static final double DEFAULT_PRODUCT_WEIGHT_KG = 15.0;

  public ShippingService(ShippingOptionRepository shippingOptionRepository) {
    this.shippingOptionRepository = shippingOptionRepository;
  }

  /**
   * Calcula as opções de frete disponíveis baseado nos produtos e endereço
   *
   * @param requestDTO DTO contendo endereço e produtos
   * @return Lista de opções de frete calculadas
   */
  @Transactional(readOnly = true)
  public List<ShippingOptionResponseDTO> calculateShippingOptions(ShippingCalculationRequestDTO requestDTO) {
    // Calcula o peso total estimado dos produtos
    double totalWeight = calculateTotalWeight(requestDTO.products());

    // Busca todas as opções de frete ativas
    List<ShippingOption> availableOptions = shippingOptionRepository.findAllByIsActiveTrue();

    // Filtra opções que suportam o peso e calcula o preço
    return availableOptions.stream()
        .filter(option -> isWeightWithinRange(totalWeight, option))
        .map(option -> calculateShippingPrice(option, totalWeight, requestDTO.address()))
        .collect(Collectors.toList());
  }

  /**
   * Calcula o peso total dos produtos usando o peso real de cada produto
   * Se o produto não tiver peso definido, usa o peso padrão (15kg)
   */
  private double calculateTotalWeight(Set<OrderProductResponseDTO> products) {
    return products.stream()
        .mapToDouble(orderProduct -> {
          Double productWeight = orderProduct.product().weightKg();
          double weight = (productWeight != null) ? productWeight : DEFAULT_PRODUCT_WEIGHT_KG;
          return weight * orderProduct.quantity();
        })
        .sum();
  }

  /**
   * Verifica se o peso está dentro da faixa suportada pela opção de frete
   */
  private boolean isWeightWithinRange(double weight, ShippingOption option) {
    return weight >= option.getMinWeightKg() && weight <= option.getMaxWeightKg();
  }

  /**
   * Calcula o preço do frete baseado na opção, peso e endereço
   * Por enquanto usa cálculo simples: basePrice + (peso * pricePerKg)
   * TODO: Adicionar cálculo de distância baseado no CEP
   */
  private ShippingOptionResponseDTO calculateShippingPrice(
      ShippingOption option,
      double totalWeight,
      AddressRequestDTO address) {

    // Cálculo básico: preço base + (peso * preço por kg)
    double calculatedPrice = option.getBasePrice() + (totalWeight * option.getPricePerKg());

    // Arredonda para 2 casas decimais
    BigDecimal price = BigDecimal.valueOf(calculatedPrice)
        .setScale(2, RoundingMode.HALF_UP);

    // Aplica fator de distância baseado no estado (simplificado)
    // Estados mais distantes têm um multiplicador maior
    double distanceFactor = calculateDistanceFactor(address.state());
    double finalPrice = price.doubleValue() * distanceFactor;

    return new ShippingOptionResponseDTO(
        option.getId(),
        option.getName(),
        option.getDeliveryTime(),
        BigDecimal.valueOf(finalPrice)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue());
  }

  /**
   * Calcula fator de distância baseado no estado
   * Por enquanto usa valores fixos, mas pode ser melhorado com API de CEP
   */
  private double calculateDistanceFactor(String state) {
    // Estados do Sudeste (mais próximos)
    if (state.equalsIgnoreCase("SP") || state.equalsIgnoreCase("RJ") ||
        state.equalsIgnoreCase("MG") || state.equalsIgnoreCase("ES")) {
      return 1.0;
    }

    // Estados do Sul
    if (state.equalsIgnoreCase("RS") || state.equalsIgnoreCase("SC") ||
        state.equalsIgnoreCase("PR")) {
      return 1.2;
    }

    // Estados do Centro-Oeste
    if (state.equalsIgnoreCase("DF") || state.equalsIgnoreCase("GO") ||
        state.equalsIgnoreCase("MT") || state.equalsIgnoreCase("MS")) {
      return 1.3;
    }

    // Estados do Nordeste
    if (state.equalsIgnoreCase("BA") || state.equalsIgnoreCase("CE") ||
        state.equalsIgnoreCase("PE") || state.equalsIgnoreCase("RN") ||
        state.equalsIgnoreCase("PB") || state.equalsIgnoreCase("AL") ||
        state.equalsIgnoreCase("SE") || state.equalsIgnoreCase("MA") ||
        state.equalsIgnoreCase("PI")) {
      return 1.5;
    }

    // Estados do Norte
    if (state.equalsIgnoreCase("AM") || state.equalsIgnoreCase("PA") ||
        state.equalsIgnoreCase("AC") || state.equalsIgnoreCase("RO") ||
        state.equalsIgnoreCase("RR") || state.equalsIgnoreCase("AP") ||
        state.equalsIgnoreCase("TO")) {
      return 1.8;
    }

    // Default
    return 1.2;
  }

  /**
   * Busca todas as opções de frete ativas (sem cálculo)
   */
  @Transactional(readOnly = true)
  public List<ShippingOptionResponseDTO> getAllActiveShippingOptions() {
    return shippingOptionRepository.findAllByIsActiveTrue().stream()
        .map(option -> new ShippingOptionResponseDTO(
            option.getId(),
            option.getName(),
            option.getDeliveryTime(),
            option.getBasePrice()))
        .collect(Collectors.toList());
  }
}

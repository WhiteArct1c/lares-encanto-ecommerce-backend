package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.request.shipping.ShippingCalculationRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.shipping.ShippingOptionResponseDTO;
import com.laresencanto.laresencantorestapi.service.ShippingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

  private final ShippingService shippingService;

  public ShippingController(ShippingService shippingService) {
    this.shippingService = shippingService;
  }

  /**
   * Calcula opções de frete disponíveis baseado nos produtos e endereço
   *
   * @param requestDTO DTO contendo endereço e produtos
   * @return Lista de opções de frete calculadas
   */
  @PostMapping("/calculate")
  public ResponseEntity<ResponseDTO<ShippingOptionResponseDTO>> calculateShipping(
      @RequestBody @Valid ShippingCalculationRequestDTO requestDTO) {

    List<ShippingOptionResponseDTO> options = shippingService.calculateShippingOptions(requestDTO);

    return ResponseEntity.ok(new ResponseDTO<>(
        HttpStatus.OK.toString(),
        "Opções de frete calculadas com sucesso.",
        options));
  }

  /**
   * Lista todas as opções de frete ativas (sem cálculo)
   *
   * @return Lista de opções de frete ativas
   */
  @GetMapping("/options")
  public ResponseEntity<ResponseDTO<ShippingOptionResponseDTO>> getAllShippingOptions() {
    List<ShippingOptionResponseDTO> options = shippingService.getAllActiveShippingOptions();

    return ResponseEntity.ok(new ResponseDTO<>(
        HttpStatus.OK.toString(),
        "Opções de frete retornadas com sucesso.",
        options));
  }
}

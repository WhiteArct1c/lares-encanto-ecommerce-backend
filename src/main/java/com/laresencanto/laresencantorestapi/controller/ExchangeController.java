package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeAuthorizationDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeConfirmationDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.exchange.ExchangeStatusUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.exchange.ExchangeResponseDTO;
import com.laresencanto.laresencantorestapi.service.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exchanges")
public class ExchangeController {

  private final ExchangeService exchangeService;

  public ExchangeController(ExchangeService exchangeService) {
    this.exchangeService = exchangeService;
  }

  /**
   * RF0040 - Solicitar troca/devolução (cliente)
   * POST /exchanges
   */
  @PostMapping
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> requestExchange(
      @RequestBody @Valid ExchangeRequestDTO requestDTO) {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.requestExchange(requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Listar trocas do cliente autenticado
   * GET /exchanges/my-exchanges
   */
  @GetMapping("/my-exchanges")
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> listCustomerExchanges() {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.listCustomerExchanges();
    return ResponseEntity.ok(response);
  }

  /**
   * Listar todas as trocas (admin)
   * GET /exchanges/all
   */
  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> listAllExchanges() {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.listAllExchanges();
    return ResponseEntity.ok(response);
  }

  /**
   * Listar trocas de um cliente específico
   * GET /exchanges?customerId={id}
   * - Admin: pode listar qualquer cliente ou todas (sem customerId)
   * - Cliente: pode listar apenas suas próprias trocas (deve informar seu próprio
   * customerId)
   */
  @GetMapping
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> listExchangesByCustomer(
      @RequestParam(required = false) Long customerId) {
    ResponseDTO<ExchangeResponseDTO> response;
    if (customerId != null) {
      // Lista trocas de um cliente específico (validação de permissão no service)
      response = exchangeService.listExchangesByCustomerId(customerId);
    } else {
      // Se não informar customerId, só admin pode ver todas
      response = exchangeService.listAllExchanges();
    }
    return ResponseEntity.ok(response);
  }

  /**
   * RF0042 - Visualizar todas as trocas pendentes (admin)
   * GET /exchanges/pending
   */
  @GetMapping("/pending")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> listPendingExchanges() {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.listPendingExchanges();
    return ResponseEntity.ok(response);
  }

  /**
   * RF0041 - Autorizar ou recusar troca (admin)
   * PUT /exchanges/authorize
   */
  @PutMapping("/authorize")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> authorizeExchange(
      @RequestBody @Valid ExchangeAuthorizationDTO requestDTO) {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.authorizeExchange(requestDTO);
    return ResponseEntity.ok(response);
  }

  /**
   * RF0043 - Confirmar recebimento de itens para troca (admin)
   * PUT /exchanges/confirm-receipt
   */
  @PutMapping("/confirm-receipt")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> confirmExchangeReceipt(
      @RequestBody @Valid ExchangeConfirmationDTO requestDTO) {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.confirmExchangeReceipt(requestDTO);
    return ResponseEntity.ok(response);
  }

  /**
   * Atualizar status de uma troca diretamente (admin)
   * PUT /exchanges/status
   */
  @PutMapping("/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO<ExchangeResponseDTO>> updateExchangeStatus(
      @RequestBody @Valid ExchangeStatusUpdateDTO requestDTO) {
    ResponseDTO<ExchangeResponseDTO> response = exchangeService.updateExchangeStatus(requestDTO);
    return ResponseEntity.ok(response);
  }
}

package com.laresencanto.laresencantorestapi.controller;


import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService
    ){
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> saveCustomerAddress(@RequestBody @Valid AddressUpdateRequestDTO addressUpdateRequestDTO) {
        ResponseDTO response = addressService.save(addressUpdateRequestDTO);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping
    public ResponseEntity<ResponseDTO> deleteCustomerAddress(@RequestParam String id){
        ResponseDTO response = addressService.delete(id);
        return ResponseEntity.ok(response);
    }
}

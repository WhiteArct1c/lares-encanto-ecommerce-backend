package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.product.ProductCreateDTO;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ProductResponseDTO;
import com.laresencanto.laresencantorestapi.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseDTO<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{id}")
    public ResponseDTO<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/available")
    public ResponseDTO<ProductResponseDTO> getAvailableProducts(Pageable pageable) {
        return productService.getAvailableProducts(pageable);
    }

    @GetMapping("/available/{id}")
    public ResponseDTO<ProductResponseDTO> getAvailableProductById(@PathVariable Long id) {
        return productService.getAvailableProductsById(id);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseDTO<ProductResponseDTO> createProduct(@RequestBody ProductCreateDTO dto) {
        return productService.createProduct(dto);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseDTO<ProductResponseDTO> updateProduct(@PathVariable Integer id, @RequestBody ProductUpdateDTO dto) {
        return productService.updateProduct(id, dto);
    }

    @PatchMapping("/{id}/disable")
    public ResponseDTO<Void> disableProduct(@PathVariable Integer id) {
        return productService.disableProduct(id);
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Void> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }
}

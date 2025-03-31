package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;
import com.laresencanto.laresencantorestapi.service.ProductCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    /**
     * Retrieves all registered product categories.
     */
    @GetMapping
    public ResponseDTO<ProductCategoryResponseDTO> getAllCategories() {
        return productCategoryService.getAllCategories();
    }
}

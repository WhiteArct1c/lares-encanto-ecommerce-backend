package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;
import com.laresencanto.laresencantorestapi.repository.ProductCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    /**
     * Retrieves all product categories from the database.
     *
     * @return ResponseDTO containing a list of product categories.
     */
    public ResponseDTO<ProductCategoryResponseDTO> getAllCategories() {
        List<ProductCategoryResponseDTO> categories = productCategoryRepository.findAll()
                .stream()
                .map(category -> new ProductCategoryResponseDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.OK.value()),
                "Categorias de produtos retornadas com sucesso.",
                categories
        );
    }
}

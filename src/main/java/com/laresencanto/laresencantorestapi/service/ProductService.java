package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.product.*;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductCreateDTO;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductEnableDisableDTO;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ProductResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;
import com.laresencanto.laresencantorestapi.repository.*;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

        private final ProductRepository productRepository;
        private final StockRepository stockRepository;
        private final ProductCategoryRepository productCategoryRepository;
        private final ProductStatusHistoryRepository productStatusHistoryRepository;
        private final PricingGroupRepository pricingGroupRepository;

        public ProductService(
                        ProductRepository productRepository,
                        StockRepository stockRepository,
                        ProductCategoryRepository productCategoryRepository,
                        ProductStatusHistoryRepository productStatusHistoryRepository,
                        PricingGroupRepository pricingGroupRepository) {
                this.productRepository = productRepository;
                this.stockRepository = stockRepository;
                this.productCategoryRepository = productCategoryRepository;
                this.productStatusHistoryRepository = productStatusHistoryRepository;
                this.pricingGroupRepository = pricingGroupRepository;
        }

        /**
         * Get all products from catalog
         * 
         * @param pageable pagination and sorting information.
         * @return a paginated list of all products.
         */
        public ResponseDTO<ProductResponseDTO> getAllProducts(Pageable pageable) {
                Page<ProductResponseDTO> productPage = productRepository.findAll(pageable)
                                .map(this::convertToDTO);

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Todos os produtos retornados com sucesso.",
                                productPage.getContent());
        }

        /**
         * Get a product by ID
         *
         * @param id - the product id
         * @return the product on ResponseDTO
         */
        public ResponseDTO<ProductResponseDTO> getProductById(Long id) {
                Optional<Product> product = productRepository.findById(id);
                if (product.isPresent()) {
                        ProductResponseDTO productResponseDTO = convertToDTO(product.get());

                        return new ResponseDTO<>(
                                        HttpStatus.OK.toString(),
                                        "Produto encontrado.",
                                        List.of(productResponseDTO));
                }

                return new ResponseDTO<>(
                                HttpStatus.NOT_FOUND.toString(),
                                "Produto não encontrado.",
                                null);
        }

        /**
         * Retrieves a paginated list of available products (active and in stock).
         *
         * @param pageable pagination and sorting information.
         * @return a paginated list of available products.
         */
        public ResponseDTO<ProductResponseDTO> getAllAvailableProducts(Pageable pageable) {
                Page<ProductResponseDTO> productPage = productRepository.findAvailableProducts(pageable)
                                .map(this::convertToDTO);

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Produtos disponíveis retornados com sucesso.",
                                productPage.getContent());
        }

        /**
         * Get an available product by id
         *
         * @param id - the available product id
         * @return the product on ResponseDTO
         */
        public ResponseDTO<ProductResponseDTO> getAvailableProductById(Long id) {
                Optional<Product> product = productRepository.findAvailableProductById(id);
                if (product.isPresent()) {
                        ProductResponseDTO productResponseDTO = convertToDTO(product.get());
                        return new ResponseDTO<>(
                                        HttpStatus.OK.toString(),
                                        "Produto disponível encontrado.",
                                        List.of(productResponseDTO));
                }

                return new ResponseDTO<>(
                                HttpStatus.NOT_FOUND.toString(),
                                "Produto não disponível ou não encontrado.",
                                null);
        }

        /**
         * Creates a new product along with its initial stock.
         *
         * @param dto the DTO containing product details and initial stock quantity.
         * @return the created product as a DTO.
         */
        public ResponseDTO<ProductResponseDTO> createProduct(ProductCreateDTO dto) {
                Optional<ProductCategory> categoryOpt = productCategoryRepository.findById(dto.categoryId());
                Optional<PricingGroup> pricingGroupOpt = pricingGroupRepository.findById(dto.pricingGroupId());

                if (categoryOpt.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Categoria não encontrada.",
                                        null);
                }

                if (pricingGroupOpt.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Grupo de precificação não encontrado.",
                                        null);
                }

                Product product = new Product();
                product.setName(dto.name());
                product.setDescription(dto.description());
                product.setPrice(dto.price());
                product.setColor(dto.color());
                product.setIsActive(true);
                product.setCategory(categoryOpt.get());
                product.setPricingGroup(pricingGroupOpt.get());
                product.setType(dto.type());
                product.setWeightKg(dto.weightKg() != null ? dto.weightKg() : 15.0); // Peso padrão se não fornecido
                // Calcula preço de venda com base no grupo de precificação
                product.setSalePrice(
                                calculateSalePrice(
                                                product.getPrice(),
                                                BigDecimal.valueOf(product.getPricingGroup().getProfitMargin())));

                MultipartFile imageFile = dto.image();
                if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                                product.setImage(imageFile.getBytes());
                        } catch (IOException e) {
                                return new ResponseDTO<>(
                                                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                                "Erro ao processar a imagem.",
                                                null);
                        }
                }

                Product savedProduct = productRepository.save(product);

                Stock stock = new Stock();
                stock.setProduct(savedProduct);
                stock.setQuantity(dto.initialStockQuantity());
                stock.setReservedQuantity(0);
                stockRepository.save(stock);

                return new ResponseDTO<>(
                                HttpStatus.CREATED.toString(),
                                "Produto criado com sucesso.",
                                List.of(convertToDTO(savedProduct)));
        }

        /**
         * Updates an existing product.
         *
         * @param id  the ID of the product to be updated.
         * @param dto the DTO containing updated product details.
         * @return the updated product as a DTO.
         */
        public ResponseDTO<ProductResponseDTO> updateProduct(Integer id, ProductUpdateDTO dto) {
                Optional<Product> productOpt = productRepository.findById(Long.valueOf(id));

                if (productOpt.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Produto não encontrado.",
                                        null);
                }

                Product product = productOpt.get();
                
                if (dto.name() != null) {
                        product.setName(dto.name());
                }
                if (dto.description() != null) {
                        product.setDescription(dto.description());
                }
                if (dto.price() != null) {
                        product.setPrice(dto.price());
                }
                if (dto.color() != null) {
                        product.setColor(dto.color());
                }
                if (dto.isActive() != null) {
                        product.setIsActive(dto.isActive());
                }
                if (dto.type() != null) {
                        product.setType(dto.type());
                }
                if (dto.weightKg() != null) {
                        product.setWeightKg(dto.weightKg());
                }
                
                if (dto.categoryId() != null) {
                        Optional<ProductCategory> categoryOpt = productCategoryRepository.findById(dto.categoryId());
                        if (categoryOpt.isEmpty()) {
                                return new ResponseDTO<>(
                                                HttpStatus.NOT_FOUND.toString(),
                                                "Categoria não encontrada.",
                                                null);
                        }
                        product.setCategory(categoryOpt.get());
                }
                
                if (dto.pricingGroupId() != null) {
                        Optional<PricingGroup> pricingGroupOpt = pricingGroupRepository.findById(dto.pricingGroupId());
                        if (pricingGroupOpt.isEmpty()) {
                                return new ResponseDTO<>(
                                                HttpStatus.NOT_FOUND.toString(),
                                                "Grupo de precificação não encontrado.",
                                                null);
                        }
                        product.setPricingGroup(pricingGroupOpt.get());
                }
                
                if (dto.price() != null || dto.pricingGroupId() != null) {
                        product.setSalePrice(
                                        calculateSalePrice(
                                                        product.getPrice(),
                                                        BigDecimal.valueOf(product.getPricingGroup().getProfitMargin())));
                }

                MultipartFile imageFile = dto.image();
                if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                                product.setImage(imageFile.getBytes());
                        } catch (IOException e) {
                                return new ResponseDTO<>(
                                                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                                "Erro ao processar a imagem.",
                                                null);
                        }
                }

                if (dto.stockQuantity() != null) {
                        Optional<Stock> stockOpt = stockRepository.findByProductId(id);
                        if (stockOpt.isEmpty()) {
                                return new ResponseDTO<>(
                                                HttpStatus.NOT_FOUND.toString(),
                                                "Estoque do produto não encontrado.",
                                                null);
                        }
                        Stock stock = stockOpt.get();
                        if (dto.stockQuantity() < stock.getReservedQuantity()) {
                                return new ResponseDTO<>(
                                                HttpStatus.BAD_REQUEST.toString(),
                                                "Quantidade em estoque não pode ser menor que a quantidade reservada.",
                                                null);
                        }
                        stock.setQuantity(dto.stockQuantity());
                        stockRepository.save(stock);
                }

                Product updatedProduct = productRepository.save(product);

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Produto atualizado com sucesso.",
                                List.of(convertToDTO(updatedProduct)));
        }

        /**
         * Enables a product by stting its active status to true
         *
         * @param dto the dto to enable a product which contains an id and a reason
         */
        public ResponseDTO<Void> enableProduct(ProductEnableDisableDTO dto) {
                Optional<Product> productOpt = productRepository.findById(Long.valueOf(dto.id()));
                if (productOpt.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Produto não encontrado.",
                                        null);
                } else if (productOpt.get().getIsActive()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Produto já se encontra ativado.",
                                        null);
                }

                Product product = productOpt.get();
                ProductStatusHistory statusHistory = new ProductStatusHistory();

                statusHistory.setProduct(product);
                statusHistory.setPreviousStatus(product.getIsActive());
                product.setIsActive(true);
                statusHistory.setNewStatus(true);
                statusHistory.setReason(dto.reason());

                productRepository.save(product);
                productStatusHistoryRepository.save(statusHistory);

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Produto ativado com sucesso.",
                                null);

        }

        /**
         * Disables a product by setting its active status to false (logical deletion).
         *
         * @param dto the dto to disable a product which contains an id and a reason
         */
        public ResponseDTO<Void> disableProduct(ProductEnableDisableDTO dto) {
                Optional<Product> productOpt = productRepository.findById(Long.valueOf(dto.id()));
                if (productOpt.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Produto não encontrado.",
                                        null);
                } else if (!productOpt.get().getIsActive()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Produto já se encontra desativado.",
                                        null);
                }

                Product product = productOpt.get();
                ProductStatusHistory statusHistory = new ProductStatusHistory();

                statusHistory.setProduct(product);
                statusHistory.setPreviousStatus(product.getIsActive());
                product.setIsActive(false);
                statusHistory.setNewStatus(false);
                statusHistory.setReason(dto.reason());

                productRepository.save(product);
                productStatusHistoryRepository.save(statusHistory);

                return new ResponseDTO<>(
                                HttpStatus.OK.toString(),
                                "Produto desativado com sucesso.",
                                null);
        }

        /**
         * Permanently deletes a product along with its stock.
         *
         * @param id the ID of the product to be deleted.
         */
        public ResponseDTO<Void> deleteProduct(Integer id) {
                Optional<Product> productOpt = productRepository.findById(Long.valueOf(id));
                if (productOpt.isEmpty()) {
                        return new ResponseDTO<>(
                                        HttpStatus.NOT_FOUND.toString(),
                                        "Produto não encontrado.",
                                        null);
                }

                stockRepository.findByProductId(id).ifPresent(stockRepository::delete);
                productRepository.deleteById(Long.valueOf(id));

                return new ResponseDTO<>(
                                HttpStatus.NO_CONTENT.toString(),
                                "Produto removido com sucesso.",
                                null);
        }

        /**
         * Calculates the sale price using the price and the pricing group profit margin
         *
         * @param price        The product price without the profit margin
         * @param profitMargin The % of the pricing group profit margin
         * @return the product's sale price
         */
        private BigDecimal calculateSalePrice(BigDecimal price, BigDecimal profitMargin) {
                BigDecimal factorMarge = profitMargin
                                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                                .add(BigDecimal.ONE);

                return price.multiply(factorMarge).setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * Converts a Product entity to a ProductResponseDTO.
         *
         * @param product the product entity to convert.
         * @return the corresponding DTO with product details and stock quantity.
         */
        private ProductResponseDTO convertToDTO(Product product) {
                Integer stockQuantity = stockRepository.findByProductId(product.getId())
                                .map(Stock::getQuantity)
                                .orElse(0);

                PricingGroupResponseDTO pricingGroup = new PricingGroupResponseDTO(
                                product.getPricingGroup().getId(),
                                product.getPricingGroup().getName(),
                                product.getPricingGroup().getProfitMargin());

                ProductCategoryResponseDTO productCategory = new ProductCategoryResponseDTO(
                                product.getCategory().getId(),
                                product.getCategory().getName());

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
                                product.getWeightKg());
        }

        /**
         * Converts a byte[] to a base64 string with image type using apache tika
         *
         * @param image the byte[] product image
         * @return a string with the base64 product image string with the image type, or
         *         null if image is null
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
}

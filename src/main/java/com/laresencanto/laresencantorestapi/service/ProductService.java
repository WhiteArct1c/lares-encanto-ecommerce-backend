package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.Product;
import com.laresencanto.laresencantorestapi.domain.ProductCategory;
import com.laresencanto.laresencantorestapi.domain.ProductStatusHistory;
import com.laresencanto.laresencantorestapi.domain.Stock;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductCreateDTO;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductEnableDisableDTO;
import com.laresencanto.laresencantorestapi.dto.request.product.ProductUpdateDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ProductResponseDTO;
import com.laresencanto.laresencantorestapi.repository.ProductCategoryRepository;
import com.laresencanto.laresencantorestapi.repository.ProductRepository;
import com.laresencanto.laresencantorestapi.repository.ProductStatusHistoryRepository;
import com.laresencanto.laresencantorestapi.repository.StockRepository;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductStatusHistoryRepository productStatusHistoryRepository;

    public ProductService(
            ProductRepository productRepository,
            StockRepository stockRepository,
            ProductCategoryRepository productCategoryRepository,
            ProductStatusHistoryRepository productStatusHistoryRepository
    ) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productStatusHistoryRepository = productStatusHistoryRepository;
    }

    /**
     * Get all products from catalog
     * @param pageable pagination and sorting information.
     * @return a paginated list of all products.
     */
    public ResponseDTO<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Page<ProductResponseDTO> productPage = productRepository.findAll(pageable)
                .map(this::convertToDTO);

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.OK.value()),
                "Todos os produtos retornados com sucesso.",
                productPage.getContent()
        );
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
                    String.valueOf(HttpStatus.OK.value()),
                    "Produto encontrado.",
                    List.of(productResponseDTO)
            );
        }

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                "Produto não encontrado.",
                null
        );
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
                String.valueOf(HttpStatus.OK.value()),
                "Produtos disponíveis retornados com sucesso.",
                productPage.getContent()
        );
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
                    String.valueOf(HttpStatus.OK.value()),
                    "Produto disponível encontrado.",
                    List.of(productResponseDTO)
            );
        }

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                "Produto não disponível ou não encontrado.",
                null
        );
    }

    /**
     * Creates a new product along with its initial stock.
     *
     * @param dto the DTO containing product details and initial stock quantity.
     * @return the created product as a DTO.
     */
    public ResponseDTO<ProductResponseDTO> createProduct(ProductCreateDTO dto) {
        Optional<ProductCategory> categoryOpt = productCategoryRepository.findById(dto.categoryId());
        if (categoryOpt.isEmpty()) {
            return new ResponseDTO<>(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Categoria não encontrada.",
                    null
            );
        }

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setColor(dto.color());
        product.setIsActive(true);
        product.setCategory(categoryOpt.get());
        product.setType(dto.type());

        MultipartFile imageFile = dto.image();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                product.setImage(imageFile.getBytes());
            } catch (IOException e) {
                return new ResponseDTO<>(
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "Erro ao processar a imagem.",
                        null
                );
            }
        }

        Product savedProduct = productRepository.save(product);

        Stock stock = new Stock();
        stock.setProduct(savedProduct);
        stock.setQuantity(dto.initialStockQuantity());
        stockRepository.save(stock);

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.CREATED.value()),
                "Produto criado com sucesso.",
                List.of(convertToDTO(savedProduct))
        );
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
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    "Produto não encontrado.",
                    null
            );
        }

        Product product = productOpt.get();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setColor(dto.color());
        product.setIsActive(dto.isActive());
        product.setType(dto.type());

        if (dto.categoryId() != null) {
            Optional<ProductCategory> categoryOpt = productCategoryRepository.findById(dto.categoryId());
            if (categoryOpt.isEmpty()) {
                return new ResponseDTO<>(
                        String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        "Categoria não encontrada.",
                        null
                );
            }
            product.setCategory(categoryOpt.get());
        }

        MultipartFile imageFile = dto.image();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                product.setImage(imageFile.getBytes());
            } catch (IOException e) {
                return new ResponseDTO<>(
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "Erro ao processar a imagem.",
                        null
                );
            }
        }

        Product updatedProduct = productRepository.save(product);

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.OK.value()),
                "Produto atualizado com sucesso.",
                List.of(convertToDTO(updatedProduct))
        );
    }


    /**
     * Enables a product by stting its active status to true
     *
     * @param dto the dto to enable a product which contains an id and a reason
     */
    public ResponseDTO<Void> enableProduct(ProductEnableDisableDTO dto) {
        Optional<Product> productOpt = productRepository.findById(Long.valueOf(dto.id()));
        if(productOpt.isEmpty()){
            return new ResponseDTO<>(
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    "Produto não encontrado.",
                    null
            );
        } else if (productOpt.get().getIsActive()) {
            return new ResponseDTO<>(
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    "Produto já se encontra ativado.",
                    null
            );
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
                String.valueOf(HttpStatus.OK.value()),
                "Produto ativado com sucesso.",
                null
        );

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
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    "Produto não encontrado.",
                    null
            );
        } else if (!productOpt.get().getIsActive()) {
            return new ResponseDTO<>(
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    "Produto já se encontra desativado.",
                    null
            );
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
                String.valueOf(HttpStatus.OK.value()),
                "Produto desativado com sucesso.",
                null
        );
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
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    "Produto não encontrado.",
                    null
            );
        }

        stockRepository.findByProductId(id).ifPresent(stockRepository::delete);
        productRepository.deleteById(Long.valueOf(id));

        return new ResponseDTO<>(
                String.valueOf(HttpStatus.NO_CONTENT.value()),
                "Produto removido com sucesso.",
                null
        );
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

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getColor(),
                convertByteToBase64String(product.getImage()),
                product.getIsActive(),
                product.getCategory().getName(),
                product.getType(),
                stockQuantity
        );
    }

    /**
     * Converts a byte[] to a base64 string with image type using apache tika
     *
     * @param image the byte[] product image
     * @return a string with the base64 product image string with the image type
     */
    private String convertByteToBase64String(byte[] image) {
        Tika tika = new Tika();

        String base64Image = (image != null) ? Base64.getEncoder().encodeToString(image) : null;
        String type = tika.detect(image);

        return "data:" + type + ";base64," + base64Image;
    }
}

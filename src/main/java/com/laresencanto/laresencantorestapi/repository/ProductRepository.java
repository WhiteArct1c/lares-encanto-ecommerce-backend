package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND EXISTS (SELECT s FROM Stock s WHERE s.product.id = p.id AND s.quantity > 0)")
    Page<Product> findAvailableProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND EXISTS (SELECT s FROM Stock s WHERE s.product.id = p.id AND s.quantity > 0) AND p.id = ?1")
    Optional<Product> findAvailableProductById(Long id);

}

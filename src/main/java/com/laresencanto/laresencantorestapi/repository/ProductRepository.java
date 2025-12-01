package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"colors", "tags", "category", "pricingGroup"})
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.colors " +
           "LEFT JOIN FETCH p.tags " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.pricingGroup " +
           "WHERE p.isActive = true AND EXISTS (SELECT s FROM Stock s WHERE s.product.id = p.id AND s.quantity > 0)")
    Page<Product> findAvailableProducts(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.colors " +
           "LEFT JOIN FETCH p.tags " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.pricingGroup " +
           "WHERE p.isActive = true AND EXISTS (SELECT s FROM Stock s WHERE s.product.id = p.id AND s.quantity > 0) AND p.id = ?1")
    Optional<Product> findAvailableProductById(Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.colors " +
           "LEFT JOIN FETCH p.tags " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.pricingGroup")
    List<Product> findAllWithColorsAndTags();

}

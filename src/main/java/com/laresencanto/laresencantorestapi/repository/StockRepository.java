package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Integer> {

    Optional<Stock> findByProductId(Integer productId);

}
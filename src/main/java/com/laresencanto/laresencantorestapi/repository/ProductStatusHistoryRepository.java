package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.product.ProductStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusHistoryRepository extends JpaRepository<ProductStatusHistory, Long> {
}

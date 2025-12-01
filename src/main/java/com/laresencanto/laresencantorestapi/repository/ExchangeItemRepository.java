package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.exchange.ExchangeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeItemRepository extends JpaRepository<ExchangeItem, Long> {
    
    List<ExchangeItem> findAllByExchangeId(Long exchangeId);
}


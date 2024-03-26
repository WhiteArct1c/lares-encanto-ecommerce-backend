package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<List<CreditCard>> findAllByCustomerId (Long id);
}

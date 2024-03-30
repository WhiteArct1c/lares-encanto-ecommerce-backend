package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<List<CreditCard>> findAllByCustomerId (Long id);
}

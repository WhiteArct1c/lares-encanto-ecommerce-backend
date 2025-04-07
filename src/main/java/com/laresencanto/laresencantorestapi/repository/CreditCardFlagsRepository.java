package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.creditCard.CreditCardFlags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardFlagsRepository extends JpaRepository<CreditCardFlags, Long> {
}

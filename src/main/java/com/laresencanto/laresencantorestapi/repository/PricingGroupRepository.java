package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.product.PricingGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingGroupRepository extends JpaRepository<PricingGroup, Integer> {
}

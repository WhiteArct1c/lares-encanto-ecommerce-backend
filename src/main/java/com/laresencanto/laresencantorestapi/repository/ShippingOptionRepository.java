package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.shipping.ShippingOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingOptionRepository extends JpaRepository<ShippingOption, Long> {
    List<ShippingOption> findAllByIsActiveTrue();
}


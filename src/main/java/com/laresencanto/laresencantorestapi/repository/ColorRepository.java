package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    Optional<Color> findByHexCode(String hexCode);
    Optional<Color> findByNameIgnoreCase(String name);
}



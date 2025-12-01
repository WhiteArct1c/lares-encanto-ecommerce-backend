package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.product.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByNameIgnoreCase(String name);
}



package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Long> {
    Gender findByName(String name);
}

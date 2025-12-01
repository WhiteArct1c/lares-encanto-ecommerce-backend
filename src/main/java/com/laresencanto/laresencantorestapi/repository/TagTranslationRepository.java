package com.laresencanto.laresencantorestapi.repository;

import com.laresencanto.laresencantorestapi.domain.product.TagTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagTranslationRepository extends JpaRepository<TagTranslation, Integer> {
    
    Optional<TagTranslation> findByTagIdAndLanguage(Integer tagId, String language);
    
    boolean existsByTagIdAndLanguage(Integer tagId, String language);
}


package com.laresencanto.laresencantorestapi.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.laresencanto.laresencantorestapi.domain.product.Tag;
import com.laresencanto.laresencantorestapi.domain.product.TagTranslation;
import com.laresencanto.laresencantorestapi.repository.TagTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    private static final String TARGET_LANGUAGE = "en"; // Inglês
    private static final String SOURCE_LANGUAGE = "pt"; // Português

    private final TagTranslationRepository tagTranslationRepository;
    private Translate translate;

    public TranslationService(TagTranslationRepository tagTranslationRepository) {
        this.tagTranslationRepository = tagTranslationRepository;
        initializeTranslateClient();
    }

    /**
     * Inicializa o cliente do Google Cloud Translation
     */
    private void initializeTranslateClient() {
        try {
            // Usa as mesmas credenciais do Google Cloud Vision
            // GOOGLE_APPLICATION_CREDENTIALS deve estar configurado
            this.translate = TranslateOptions.getDefaultInstance().getService();
            logger.info("Google Cloud Translation client initialized successfully");
        } catch (Exception e) {
            logger.warn("Failed to initialize Google Cloud Translation client. " +
                    "Translation will be disabled. Error: {}", e.getMessage());
            this.translate = null;
        }
    }

    /**
     * Traduz uma tag do português para inglês
     * Primeiro verifica no cache/banco, se não encontrar, usa a API
     * 
     * @param tag Tag a ser traduzida
     * @return Tradução em inglês ou o nome original se falhar
     */
    @Transactional
    public String translateTag(Tag tag) {
        if (tag == null || tag.getName() == null) {
            return null;
        }

        // Verifica se já existe tradução no banco
        Optional<TagTranslation> existingTranslation = 
                tagTranslationRepository.findByTagIdAndLanguage(tag.getId(), TARGET_LANGUAGE);
        
        if (existingTranslation.isPresent()) {
            return existingTranslation.get().getTranslation();
        }

        // Se não existe e a API não está disponível, retorna o nome original
        if (translate == null) {
            logger.debug("Translation API not available, returning original tag name: {}", tag.getName());
            return tag.getName();
        }

        try {
            // Traduz usando Google Cloud Translation API
            Translation translation = translate.translate(
                    tag.getName(),
                    Translate.TranslateOption.sourceLanguage(SOURCE_LANGUAGE),
                    Translate.TranslateOption.targetLanguage(TARGET_LANGUAGE)
            );

            String translatedText = translation.getTranslatedText();
            
            // Salva no banco para cache
            TagTranslation tagTranslation = new TagTranslation();
            tagTranslation.setTag(tag);
            tagTranslation.setLanguage(TARGET_LANGUAGE);
            tagTranslation.setTranslation(translatedText);
            tagTranslationRepository.save(tagTranslation);

            logger.debug("Translated tag '{}' to '{}'", tag.getName(), translatedText);
            return translatedText;

        } catch (Exception e) {
            logger.error("Error translating tag '{}': {}", tag.getName(), e.getMessage());
            // Em caso de erro, retorna o nome original
            return tag.getName();
        }
    }

    /**
     * Traduz múltiplas tags de uma vez (mais eficiente)
     * 
     * @param tags Lista de tags a traduzir
     * @return Set com traduções (em inglês)
     */
    @Transactional
    public Set<String> translateTags(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> translations = new HashSet<>();
        List<String> tagsToTranslate = new ArrayList<>();

        // Separa tags que já têm tradução das que precisam traduzir
        for (Tag tag : tags) {
            Optional<TagTranslation> existingTranslation = 
                    tagTranslationRepository.findByTagIdAndLanguage(tag.getId(), TARGET_LANGUAGE);
            
            if (existingTranslation.isPresent()) {
                translations.add(existingTranslation.get().getTranslation());
            } else {
                tagsToTranslate.add(tag.getName());
            }
        }

        // Traduz as que faltam em batch (se a API estiver disponível)
        if (!tagsToTranslate.isEmpty() && translate != null) {
            try {
                List<Translation> batchTranslations = translate.translate(
                        tagsToTranslate,
                        Translate.TranslateOption.sourceLanguage(SOURCE_LANGUAGE),
                        Translate.TranslateOption.targetLanguage(TARGET_LANGUAGE)
                );

                // Salva traduções no banco e adiciona ao resultado
                int index = 0;
                for (Tag tag : tags) {
                    if (!tagTranslationRepository.existsByTagIdAndLanguage(tag.getId(), TARGET_LANGUAGE)) {
                        if (index < batchTranslations.size()) {
                            Translation translation = batchTranslations.get(index);
                            String translatedText = translation.getTranslatedText();

                            TagTranslation tagTranslation = new TagTranslation();
                            tagTranslation.setTag(tag);
                            tagTranslation.setLanguage(TARGET_LANGUAGE);
                            tagTranslation.setTranslation(translatedText);
                            tagTranslationRepository.save(tagTranslation);

                            translations.add(translatedText);
                            index++;
                        }
                    }
                }

                logger.debug("Translated {} tags in batch", batchTranslations.size());

            } catch (Exception e) {
                logger.error("Error in batch translation: {}", e.getMessage());
                // Em caso de erro, adiciona os nomes originais
                translations.addAll(tagsToTranslate);
            }
        } else if (!tagsToTranslate.isEmpty()) {
            // Se a API não está disponível, adiciona os nomes originais
            translations.addAll(tagsToTranslate);
        }

        return translations;
    }

    /**
     * Obtém traduções de uma palavra (incluindo a própria palavra e traduções do banco)
     * Usado pelo ProductSearchService para matching
     */
    public Set<String> getTranslationsForMatching(String word, Set<Tag> productTags) {
        Set<String> translations = new HashSet<>();
        translations.add(word.toLowerCase()); // Adiciona a própria palavra

        if (productTags != null) {
            for (Tag tag : productTags) {
                String normalizedTag = tag.getName().toLowerCase();
                if (normalizedTag.equals(word.toLowerCase())) {
                    // Se a tag corresponde à palavra, busca sua tradução
                    Optional<TagTranslation> translation = 
                            tagTranslationRepository.findByTagIdAndLanguage(tag.getId(), TARGET_LANGUAGE);
                    if (translation.isPresent()) {
                        translations.add(translation.get().getTranslation().toLowerCase());
                    }
                }
            }
        }

        return translations;
    }
}


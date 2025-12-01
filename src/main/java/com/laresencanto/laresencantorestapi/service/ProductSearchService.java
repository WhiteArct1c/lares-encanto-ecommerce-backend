package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.product.Product;
import com.laresencanto.laresencantorestapi.domain.product.Color;
import com.laresencanto.laresencantorestapi.domain.product.Tag;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ColorResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.ImageSearchResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.product.TagResponseDTO;
import com.laresencanto.laresencantorestapi.repository.ProductRepository;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ProductSearchService.class);

    // Lista de termos relacionados a móveis (em inglês, pois Google Vision retorna em inglês)
    private static final Set<String> FURNITURE_KEYWORDS = Set.of(
            "furniture", "chair", "sofa", "couch", "table", "desk", "bed", "wardrobe", 
            "cabinet", "shelf", "shelving", "stool", "bench", "ottoman", "armchair", 
            "recliner", "dresser", "nightstand", "night stand", "bookshelf", "bookcase",
            "dining table", "coffee table", "side table", "end table", "console table",
            "chest", "drawer", "closet", "armoire", "headboard", "footboard", "mattress", 
            "bed frame", "bedframe", "bunk bed", "office chair", "desk chair", "bar stool", 
            "counter stool", "dining chair", "dining set", "dining room", "living room", 
            "bedroom", "office furniture", "outdoor furniture", "garden furniture", 
            "patio furniture", "deck chair", "rocking chair", "swivel chair", "lounge chair", 
            "chaise lounge", "futon", "daybed", "trundle bed", "platform bed", "storage bed",
            "bureau", "chest of drawers", "vanity", "mirror", "dressing table", "sideboard", 
            "buffet", "hutch", "china cabinet", "display cabinet", "tv stand", "tv cabinet", 
            "entertainment center", "media console", "rack", "shelving unit", "display shelf", 
            "floating shelf", "wall shelf", "corner shelf", "kitchen island", "kitchen cabinet", 
            "pantry", "bar cart", "bar cabinet", "wine rack", "wine cabinet", "liquor cabinet"
    );

    private final ImageAnalysisService imageAnalysisService;
    private final ProductRepository productRepository;
    private final TranslationService translationService;

    public ProductSearchService(
            ImageAnalysisService imageAnalysisService,
            ProductRepository productRepository,
            TranslationService translationService) {
        this.imageAnalysisService = imageAnalysisService;
        this.productRepository = productRepository;
        this.translationService = translationService;
    }

    public ResponseDTO<ImageSearchResponseDTO> searchByImage(
            MultipartFile imageFile) {

        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return new ResponseDTO<>(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Imagem não fornecida ou vazia.",
                        null);
            }

            byte[] imageBytes = imageFile.getBytes();

            ImageAnalysisService.ImageAnalysisResult analysisResult = imageAnalysisService.analyzeImage(imageBytes);

            // Valida se a imagem contém móveis
            if (analysisResult != null) {
                if (!isFurnitureImage(analysisResult)) {
                    logger.warn("Image rejected: does not contain furniture. Labels: {}, Objects: {}", 
                            analysisResult.getLabels(), analysisResult.getObjects());
                    return new ResponseDTO<>(
                            HttpStatus.BAD_REQUEST.toString(),
                            "A imagem enviada não parece conter móveis. Por favor, envie uma imagem de móveis (cadeiras, sofás, mesas, camas, armários, etc.).",
                            null);
                }
            } else {
                // Se a API não estiver disponível, não podemos validar
                logger.warn("Vision API not available, cannot validate if image contains furniture. Proceeding with basic analysis.");
                // Podemos optar por rejeitar ou permitir. Vou permitir mas avisar no log
            }

            // Busca produtos com cores e tags carregadas
            List<Product> allProducts = productRepository.findAllWithColorsAndTags();
            List<Product> availableProducts = allProducts.stream()
                    .filter(p -> p.getIsActive() != null && p.getIsActive())
                    .collect(Collectors.toList());

            List<ImageSearchResponseDTO.ProductMatchDTO> matches;
            String searchMethod;

            if (analysisResult != null) {
                matches = searchByAIFeatures(analysisResult, availableProducts);
                searchMethod = "AI_VISION";
                logger.info("Search completed using AI Vision API. Found {} matches", matches.size());
            } else {
                matches = searchByBasicImageAnalysis(imageBytes, availableProducts);
                searchMethod = "BASIC_FALLBACK";
                logger.info("Search completed using basic fallback. Found {} matches", matches.size());
            }

            List<String> labels = analysisResult != null ? analysisResult.getLabels() : new ArrayList<>();
            List<String> colors = analysisResult != null ? analysisResult.getColors() : new ArrayList<>();
            List<String> objects = analysisResult != null ? analysisResult.getObjects() : new ArrayList<>();

            ImageSearchResponseDTO response = new ImageSearchResponseDTO(
                    labels,
                    colors,
                    objects,
                    matches,
                    searchMethod);

            return new ResponseDTO<>(
                    HttpStatus.OK.toString(),
                    "Busca realizada com sucesso.",
                    List.of(response));

        } catch (Exception e) {
            logger.error("Error searching products by image", e);
            return new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "Erro ao processar busca por imagem: " + e.getMessage(),
                    null);
        }
    }

    private List<ImageSearchResponseDTO.ProductMatchDTO> searchByAIFeatures(
            ImageAnalysisService.ImageAnalysisResult analysisResult,
            List<Product> products) {

        List<ImageSearchResponseDTO.ProductMatchDTO> matches = new ArrayList<>();

        for (Product product : products) {
            double similarityScore = 0.0;
            List<String> matchReasons = new ArrayList<>();

            List<String> labels = analysisResult.getLabels();
            List<String> colors = analysisResult.getColors();
            List<String> objects = analysisResult.getObjects();

            // Flags para controlar matches de alto nível (tipo/objeto e tags)
            boolean hasTypeOrObjectMatch = false;
            boolean hasTagMatch = false;

            // 1. MATCH DE TIPO/OBJETO (Peso: 40% - mais importante)
            if (product.getType() != null) {
                String productType = normalizeString(product.getType());
                Set<String> typeTranslations = getTranslations(productType, product.getTags());

                // Match exato com objeto detectado (maior peso) - considera traduções
                for (String object : objects) {
                    String normalizedObject = normalizeString(object);
                    Set<String> objectTranslations = getTranslations(normalizedObject, product.getTags());

                    double matchScore = calculateStringSimilarity(productType, normalizedObject);

                    // Verifica match via traduções
                    Set<String> typeTranslationsForObject = new HashSet<>(typeTranslations);
                    typeTranslationsForObject.retainAll(objectTranslations);
                    boolean translationMatch = !typeTranslationsForObject.isEmpty();
                    if (translationMatch) {
                        matchScore = Math.max(matchScore, 0.95); // Boost para match via tradução
                    }

                    if (matchScore >= 1.0 || translationMatch) {
                        similarityScore += 0.4;
                        hasTypeOrObjectMatch = true;
                        String matchType = translationMatch ? "objeto traduzido" : "objeto exato";
                        matchReasons.add(matchType + ": " + object);
                        break;
                    } else if (matchScore >= 0.7) {
                        similarityScore += 0.35;
                        hasTypeOrObjectMatch = true;
                        matchReasons.add("objeto similar: " + object);
                        break;
                    } else if (matchScore >= 0.5) {
                        similarityScore += 0.25;
                        hasTypeOrObjectMatch = true;
                        matchReasons.add("objeto parcial: " + object);
                        break;
                    }
                }

                // Match com labels (considera similaridade e traduções)
                for (String label : labels) {
                    String normalizedLabel = normalizeString(label);
                    Set<String> labelTranslations = getTranslations(normalizedLabel, product.getTags());

                    double matchScore = calculateStringSimilarity(productType, normalizedLabel);

                    // Verifica match via traduções
                    Set<String> typeTranslationsForLabel = new HashSet<>(typeTranslations);
                    typeTranslationsForLabel.retainAll(labelTranslations);
                    boolean translationMatch = !typeTranslationsForLabel.isEmpty();
                    if (translationMatch) {
                        matchScore = Math.max(matchScore, 0.95); // Boost para match via tradução
                    }

                    if (matchScore >= 1.0 || translationMatch) {
                        similarityScore += 0.35;
                        hasTypeOrObjectMatch = true;
                        String matchType = translationMatch ? "tipo traduzido" : "tipo exato";
                        if (!matchReasons.contains(matchType + ": " + label)) {
                            matchReasons.add(matchType + ": " + label);
                        }
                        break;
                    } else if (matchScore >= 0.7) {
                        similarityScore += 0.3;
                        hasTypeOrObjectMatch = true;
                        if (!matchReasons.contains("tipo similar: " + label)) {
                            matchReasons.add("tipo similar: " + label);
                        }
                        break;
                    } else if (matchScore >= 0.5) {
                        similarityScore += 0.2;
                        hasTypeOrObjectMatch = true;
                        if (!matchReasons.contains("tipo parcial: " + label)) {
                            matchReasons.add("tipo parcial: " + label);
                        }
                        break;
                    }
                }
            }

            // 2. MATCH DE NOME DO PRODUTO (Peso: 45% - mais importante que tipo)
            if (product.getName() != null) {
                String productName = normalizeString(product.getName());
                String[] productWords = productName.split("\\s+");
                Set<String> significantWords = new HashSet<>();

                // Filtra palavras significativas (remove artigos, preposições, números)
                for (String word : productWords) {
                    if (word.length() > 2 && !isStopWord(word)) {
                        significantWords.add(word);
                    }
                }

                // Match com labels - considera múltiplos matches e traduções
                int labelMatches = 0;

                for (String label : labels) {
                    String normalizedLabel = normalizeString(label);
                    Set<String> labelTranslations = getTranslations(normalizedLabel, product.getTags());

                    // Match com palavras do nome (considera traduções)
                    for (String word : significantWords) {
                        Set<String> wordTranslations = getTranslations(word, product.getTags());

                        double wordScore = calculateStringSimilarity(word, normalizedLabel);

                        // Verifica match via traduções
                        Set<String> wordTranslationsForLabel = new HashSet<>(wordTranslations);
                        wordTranslationsForLabel.retainAll(labelTranslations);
                        boolean translationMatch = !wordTranslationsForLabel.isEmpty();
                        if (translationMatch) {
                            wordScore = Math.max(wordScore, 0.95); // Boost para match via tradução
                        }

                        if (wordScore >= 1.0 || translationMatch) {
                            similarityScore += 0.4;
                            String matchType = translationMatch ? "nome traduzido" : "nome exato";
                            matchReasons.add(matchType + ": " + label);
                            labelMatches++;
                            break;
                        } else if (wordScore >= 0.7) {
                            similarityScore += 0.3;
                            if (!matchReasons.contains("nome similar: " + label)) {
                                matchReasons.add("nome similar: " + label);
                            }
                            labelMatches++;
                            break;
                        } else if (wordScore >= 0.5) {
                            similarityScore += 0.2;
                            if (!matchReasons.contains("nome parcial: " + label)) {
                                matchReasons.add("nome parcial: " + label);
                            }
                            labelMatches++;
                            break;
                        }
                    }

                    // Match contextual: se nome contém palavra e label contém palavra relacionada
                    if (hasContextualMatch(productName, normalizedLabel, product.getTags())) {
                        similarityScore += 0.35;
                        if (!matchReasons.contains("contexto: " + label)) {
                            matchReasons.add("contexto: " + label);
                        }
                    }
                }

                // Match com objetos no nome (considera traduções)
                for (String object : objects) {
                    String normalizedObject = normalizeString(object);
                    Set<String> objectTranslations = getTranslations(normalizedObject, product.getTags());

                    for (String word : significantWords) {
                        Set<String> wordTranslations = getTranslations(word, product.getTags());

                        double wordScore = calculateStringSimilarity(word, normalizedObject);

                        // Verifica match via traduções
                        Set<String> wordTranslationsForObject = new HashSet<>(wordTranslations);
                        wordTranslationsForObject.retainAll(objectTranslations);
                        boolean translationMatch = !wordTranslationsForObject.isEmpty();
                        if (translationMatch) {
                            wordScore = Math.max(wordScore, 0.95); // Boost para match via tradução
                        }

                        if (wordScore >= 1.0 || translationMatch) {
                            similarityScore += 0.35;
                            String matchType = translationMatch ? "nome-objeto traduzido" : "nome-objeto exato";
                            if (!matchReasons.contains(matchType + ": " + object)) {
                                matchReasons.add(matchType + ": " + object);
                            }
                            break;
                        } else if (wordScore >= 0.7) {
                            similarityScore += 0.28;
                            if (!matchReasons.contains("nome-objeto similar: " + object)) {
                                matchReasons.add("nome-objeto similar: " + object);
                            }
                            break;
                        } else if (wordScore >= 0.5) {
                            similarityScore += 0.2;
                            if (!matchReasons.contains("nome-objeto parcial: " + object)) {
                                matchReasons.add("nome-objeto parcial: " + object);
                            }
                            break;
                        }
                    }
                }

                // Bônus por múltiplos matches no nome
                if (labelMatches > 1) {
                    similarityScore += Math.min(0.1 * (labelMatches - 1), 0.2);
                    matchReasons.add("múltiplos matches: " + labelMatches);
                }
            }

            // 3. MATCH DE CORES (Peso: 25% - aumentado e melhorado)
            if (!colors.isEmpty()) {
                int colorMatches = 0;

                // Match com cores múltiplas do produto (novo)
                if (product.getColors() != null && !product.getColors().isEmpty()) {
                    for (Color productColor : product.getColors()) {
                        for (String detectedColor : colors) {
                            if (isColorSimilar(productColor.getHexCode(), detectedColor)) {
                                similarityScore += 0.15;
                                colorMatches++;
                                if (!matchReasons.contains("cor: " + detectedColor)) {
                                    matchReasons.add("cor: " + detectedColor + " (" + productColor.getHexCode() + ")");
                                }
                            }
                        }
                    }
                }

                // Match com cor antiga (compatibilidade)
                if (product.getColor() != null && colorMatches == 0) {
                    String productColor = product.getColor().toUpperCase();
                    for (String detectedColor : colors) {
                        if (isColorSimilar(productColor, detectedColor)) {
                            similarityScore += 0.15;
                            matchReasons.add("cor: " + detectedColor);
                            break;
                        }
                    }
                }

                // Bônus por múltiplas cores correspondentes
                if (colorMatches > 1) {
                    similarityScore += Math.min(0.1 * (colorMatches - 1), 0.1);
                }
            }

            // 4. MATCH DE TAGS (Peso: 30% - muito importante!)
            if (product.getTags() != null && !product.getTags().isEmpty()) {
                int tagMatches = 0;

                // Match de tags com labels detectados
                for (Tag tag : product.getTags()) {
                    String tagName = normalizeString(tag.getName());
                    Set<String> tagTranslations = getTranslations(tagName, product.getTags());

                    // Match com labels (considera traduções)
                    for (String label : labels) {
                        String normalizedLabel = normalizeString(label);
                        Set<String> labelTranslations = getTranslations(normalizedLabel, product.getTags());

                        // Verifica match direto
                        double tagScore = calculateStringSimilarity(tagName, normalizedLabel);

                        // Verifica match via traduções
                        boolean translationMatch = false;
                        tagTranslations.retainAll(labelTranslations);
                        if (!tagTranslations.isEmpty()) {
                            translationMatch = true;
                            tagScore = Math.max(tagScore, 0.9); // Boost para match via tradução
                        }

                        if (tagScore >= 1.0 || translationMatch) {
                            similarityScore += 0.3;
                            hasTagMatch = true;
                            tagMatches++;
                            String matchType = translationMatch ? "tag traduzida" : "tag exata";
                            if (!matchReasons.contains(matchType + ": " + tag.getName())) {
                                matchReasons.add(matchType + ": " + tag.getName() + " = " + label);
                            }
                            break;
                        } else if (tagScore >= 0.7) {
                            similarityScore += 0.25;
                            hasTagMatch = true;
                            tagMatches++;
                            if (!matchReasons.contains("tag similar: " + tag.getName())) {
                                matchReasons.add("tag similar: " + tag.getName() + " ≈ " + label);
                            }
                            break;
                        } else if (tagScore >= 0.5) {
                            similarityScore += 0.15;
                            hasTagMatch = true;
                            tagMatches++;
                            if (!matchReasons.contains("tag parcial: " + tag.getName())) {
                                matchReasons.add("tag parcial: " + tag.getName() + " ~ " + label);
                            }
                            break;
                        }
                    }

                    // Match com objetos (considera traduções)
                    for (String object : objects) {
                        String normalizedObject = normalizeString(object);
                        Set<String> objectTranslations = getTranslations(normalizedObject, product.getTags());

                        // Verifica match direto
                        double tagScore = calculateStringSimilarity(tagName, normalizedObject);

                        // Verifica match via traduções
                        Set<String> tagTranslationsForObject = getTranslations(tagName, product.getTags());
                        tagTranslationsForObject.retainAll(objectTranslations);
                        boolean translationMatch = !tagTranslationsForObject.isEmpty();
                        if (translationMatch) {
                            tagScore = Math.max(tagScore, 0.9); // Boost para match via tradução
                        }

                        if (tagScore >= 1.0 || translationMatch) {
                            similarityScore += 0.3;
                            hasTagMatch = true;
                            tagMatches++;
                            String matchType = translationMatch ? "tag-objeto traduzida" : "tag-objeto exata";
                            if (!matchReasons.contains(matchType + ": " + tag.getName())) {
                                matchReasons.add(matchType + ": " + tag.getName() + " = " + object);
                            }
                            break;
                        } else if (tagScore >= 0.7) {
                            similarityScore += 0.25;
                            hasTagMatch = true;
                            tagMatches++;
                            if (!matchReasons.contains("tag-objeto similar: " + tag.getName())) {
                                matchReasons.add("tag-objeto similar: " + tag.getName() + " ≈ " + object);
                            }
                            break;
                        }
                    }
                }

                // Bônus por múltiplas tags correspondentes
                if (tagMatches > 1) {
                    similarityScore += Math.min(0.15 * (tagMatches - 1), 0.2);
                    matchReasons.add("múltiplas tags: " + tagMatches);
                }
            }

            // 5. MATCH DE DESCRIÇÃO (Peso: 10% - reduzido, tags são mais importantes)
            if (product.getDescription() != null) {
                String description = normalizeString(product.getDescription());
                int descriptionMatches = 0;

                for (String label : labels) {
                    String normalizedLabel = normalizeString(label);
                    if (label.length() > 3) {
                        if (description.contains(normalizedLabel)) {
                            descriptionMatches++;
                            if (!matchReasons.contains("descrição: " + label)) {
                                matchReasons.add("descrição: " + label);
                            }
                        } else {
                            String[] descWords = description.split("\\s+");
                            for (String descWord : descWords) {
                                if (descWord.length() > 3) {
                                    double similarity = calculateStringSimilarity(descWord, normalizedLabel);
                                    if (similarity >= 0.7) {
                                        descriptionMatches++;
                                        if (!matchReasons.contains("descrição similar: " + label)) {
                                            matchReasons.add("descrição similar: " + label);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                for (String object : objects) {
                    String normalizedObject = normalizeString(object);
                    if (description.contains(normalizedObject)) {
                        descriptionMatches++;
                        if (!matchReasons.contains("descrição-objeto: " + object)) {
                            matchReasons.add("descrição-objeto: " + object);
                        }
                    }
                }

                if (descriptionMatches > 0) {
                    similarityScore += Math.min(0.05 * descriptionMatches, 0.1);
                }
            }

            // Se não houve nenhum match de tipo/objeto nem de tags,
            // evitamos recomendar produtos apenas por cor/nome/descrição
            if (!hasTypeOrObjectMatch && !hasTagMatch) {
                similarityScore = 0.0;
            }

            // Normalizar score (máximo 1.0)
            similarityScore = Math.min(1.0, similarityScore);

            // Adicionar apenas produtos com score significativo
            if (similarityScore > 0.15) {
                matches.add(new ImageSearchResponseDTO.ProductMatchDTO(
                        product.getId(),
                        product.getName(),
                        similarityScore,
                        matchReasons,
                        convertByteToBase64String(product.getImage())));
            }
        }

        return matches.stream()
                .sorted((a, b) -> Double.compare(b.similarityScore(), a.similarityScore()))
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Calcula similaridade entre duas strings usando múltiplas métricas
     * Retorna valor entre 0.0 e 1.0
     */
    private double calculateStringSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null)
            return 0.0;
        if (str1.equals(str2))
            return 1.0;

        str1 = normalizeString(str1);
        str2 = normalizeString(str2);

        // Match exato após normalização
        if (str1.equals(str2))
            return 1.0;

        // Match se uma contém a outra (para palavras compostas)
        if (str1.contains(str2) || str2.contains(str1)) {
            double lengthRatio = Math.min(str1.length(), str2.length())
                    / (double) Math.max(str1.length(), str2.length());
            return 0.7 + (0.3 * lengthRatio); // 0.7 a 1.0
        }

        // Similaridade de Levenshtein (distância de edição)
        double levenshteinScore = 1.0 - (calculateLevenshteinDistance(str1, str2) /
                (double) Math.max(str1.length(), str2.length()));

        // Match de prefixo/sufixo
        double prefixScore = 0.0;
        int minLen = Math.min(str1.length(), str2.length());
        if (minLen > 0) {
            int prefixMatch = 0;
            for (int i = 0; i < minLen; i++) {
                if (str1.charAt(i) == str2.charAt(i)) {
                    prefixMatch++;
                } else {
                    break;
                }
            }
            prefixScore = prefixMatch / (double) minLen;
        }

        // Combina as métricas
        return Math.max(levenshteinScore, prefixScore * 0.8);
    }

    /**
     * Calcula distância de Levenshtein entre duas strings
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Obtém traduções de uma palavra usando o TranslationService
     * Busca traduções do banco de dados (cache) e inclui a própria palavra
     */
    private Set<String> getTranslations(String word,
        Set<Tag> productTags) {
        Set<String> translations = new HashSet<>();
        String normalized = normalizeString(word);
        translations.add(normalized); // Sempre inclui a própria palavra

        // Busca traduções das tags do produto que correspondem à palavra
        if (productTags != null) {
            for (Tag tag : productTags) {
                String normalizedTag = normalizeString(tag.getName());
                if (normalizedTag.equals(normalized)) {
                    // Se a tag corresponde à palavra, busca sua tradução do banco
                    Set<String> tagTranslations = translationService.getTranslationsForMatching(word, Set.of(tag));
                    translations.addAll(tagTranslations);
                }
            }
        }

        return translations;
    }

    /**
     * Verifica se há match contextual (ex: TV/television, rack/support)
     */
    private boolean hasContextualMatch(String productName, String label,
            Set<Tag> productTags) {
        String normalizedProduct = normalizeString(productName);
        String normalizedLabel = normalizeString(label);

        // Verifica se há tradução entre as palavras
        Set<String> productTranslations = getTranslations(normalizedProduct, productTags);
        Set<String> labelTranslations = getTranslations(normalizedLabel, productTags);

        // Se há interseção nas traduções, é um match
        productTranslations.retainAll(labelTranslations);
        if (!productTranslations.isEmpty()) {
            return true;
        }

        // Palavras relacionadas comuns (apenas para casos muito específicos)
        String[][] contextualPairs = {
                { "tv", "television" }, { "televisao", "television" },
                { "rack", "stand" }, { "rack", "support" },
                { "suporte", "stand" }, { "suporte", "support" }
        };

        for (String[] pair : contextualPairs) {
            if ((normalizedProduct.contains(pair[0]) && normalizedLabel.contains(pair[1])) ||
                    (normalizedProduct.contains(pair[1]) && normalizedLabel.contains(pair[0]))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se uma palavra é stop word (artigo, preposição, etc)
     */
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("de", "da", "do", "para", "com", "em", "na", "no", "a", "o", "e", "ou");
        return stopWords.contains(word.toLowerCase());
    }

    private String normalizeString(String str) {
        if (str == null)
            return "";
        return str.toLowerCase()
                .replace("á", "a").replace("à", "a").replace("ã", "a").replace("â", "a")
                .replace("é", "e").replace("è", "e").replace("ê", "e")
                .replace("í", "i").replace("ì", "i").replace("î", "i")
                .replace("ó", "o").replace("ò", "o").replace("õ", "o").replace("ô", "o")
                .replace("ú", "u").replace("ù", "u").replace("û", "u")
                .replace("ç", "c")
                .trim();
    }

    private List<ImageSearchResponseDTO.ProductMatchDTO> searchByBasicImageAnalysis(
            byte[] imageBytes,
            List<Product> products) {

        List<ImageSearchResponseDTO.ProductMatchDTO> matches = new ArrayList<>();

        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(
                    new java.io.ByteArrayInputStream(imageBytes));

            if (img == null) {
                return matches;
            }

            String dominantColor = extractDominantColor(img);

            for (Product product : products) {
                double similarityScore = 0.0;
                List<String> matchReasons = new ArrayList<>();

                if (product.getColor() != null && dominantColor != null) {
                    if (isColorSimilar(product.getColor(), dominantColor)) {
                        similarityScore += 0.5;
                        matchReasons.add("cor: " + dominantColor);
                    }
                }

                if (similarityScore > 0.1) {
                    matches.add(new ImageSearchResponseDTO.ProductMatchDTO(
                            product.getId(),
                            product.getName(),
                            similarityScore,
                            matchReasons,
                            convertByteToBase64String(product.getImage())));
                }
            }

        } catch (Exception e) {
            logger.error("Error in basic image analysis", e);
        }

        return matches.stream()
                .sorted((a, b) -> Double.compare(b.similarityScore(), a.similarityScore()))
                .limit(3)
                .collect(Collectors.toList());
    }

    private String extractDominantColor(java.awt.image.BufferedImage img) {
        Map<java.awt.Color, Integer> colorCount = new HashMap<>();

        int sampleSize = Math.min(100, img.getWidth() * img.getHeight());
        int step = Math.max(1, (img.getWidth() * img.getHeight()) / sampleSize);

        for (int i = 0; i < img.getWidth(); i += step) {
            for (int j = 0; j < img.getHeight(); j += step) {
                int rgb = img.getRGB(i, j);
                java.awt.Color color = new java.awt.Color(rgb);
                java.awt.Color quantized = new java.awt.Color(
                        (color.getRed() / 32) * 32,
                        (color.getGreen() / 32) * 32,
                        (color.getBlue() / 32) * 32);
                colorCount.put(quantized, colorCount.getOrDefault(quantized, 0) + 1);
            }
        }

        java.awt.Color dominant = colorCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(new java.awt.Color(128, 128, 128));

        return String.format("#%02X%02X%02X", dominant.getRed(), dominant.getGreen(), dominant.getBlue());
    }

    private boolean isColorSimilar(String color1, String color2) {
        try {
            Color c1 = Color.decode(color1);
            Color c2 = Color.decode(color2);

            double distance = Math.sqrt(
                    Math.pow(c1.getRed() - c2.getRed(), 2) +
                            Math.pow(c1.getGreen() - c2.getGreen(), 2) +
                            Math.pow(c1.getBlue() - c2.getBlue(), 2));

            return distance < 100;
        } catch (Exception e) {
            return color1.equalsIgnoreCase(color2);
        }
    }

    /**
     * Valida se a imagem contém móveis baseado nos labels e objetos detectados
     *
     * @param analysisResult resultado da análise da imagem pelo Google Vision
     * @return true se a imagem contém móveis, false caso contrário
     */
    private boolean isFurnitureImage(ImageAnalysisService.ImageAnalysisResult analysisResult) {
        if (analysisResult == null) {
            return false;
        }

        // Verifica labels
        for (String label : analysisResult.getLabels()) {
            String normalizedLabel = normalizeString(label);
            if (FURNITURE_KEYWORDS.contains(normalizedLabel)) {
                logger.debug("Furniture detected via label: {}", label);
                return true;
            }
        }

        // Verifica objetos detectados
        for (String object : analysisResult.getObjects()) {
            String normalizedObject = normalizeString(object);
            if (FURNITURE_KEYWORDS.contains(normalizedObject)) {
                logger.debug("Furniture detected via object: {}", object);
                return true;
            }
        }

        // Verifica também se algum label/objeto contém palavras-chave (para casos como "dining table", "office chair")
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(analysisResult.getLabels());
        allTerms.addAll(analysisResult.getObjects());

        for (String term : allTerms) {
            String normalizedTerm = normalizeString(term);
            // Verifica se o termo contém alguma palavra-chave de móveis
            for (String keyword : FURNITURE_KEYWORDS) {
                if (normalizedTerm.contains(keyword) || keyword.contains(normalizedTerm)) {
                    logger.debug("Furniture detected via partial match: {} contains {}", term, keyword);
                    return true;
                }
            }
        }

        logger.debug("No furniture detected. Labels: {}, Objects: {}", 
                analysisResult.getLabels(), analysisResult.getObjects());
        return false;
    }

    /**
     * Converte um array de bytes de imagem para uma string Base64 com data URI
     *
     * @param image o array de bytes da imagem do produto
     * @return uma string com a imagem em Base64 com o tipo de imagem, ou null se a imagem for null
     */
    private String convertByteToBase64String(byte[] image) {
        if (image == null || image.length == 0) {
            return null;
        }

        Tika tika = new Tika();
        String base64Image = Base64.getEncoder().encodeToString(image);
        String type = tika.detect(image);

        return "data:" + type + ";base64," + base64Image;
    }
}

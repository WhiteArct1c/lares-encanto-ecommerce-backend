package com.laresencanto.laresencantorestapi.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ImageAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(ImageAnalysisService.class);
    private static final int FREE_TIER_LIMIT = 1000;
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private final ConcurrentHashMap<String, ImageAnalysisResult> cache = new ConcurrentHashMap<>();
    
    private ImageAnnotatorClient visionClient;

    public ImageAnalysisService() {
        try {
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (credentialsPath == null || credentialsPath.isEmpty()) {
                logger.warn("GOOGLE_APPLICATION_CREDENTIALS not set. Google Vision API will not be available. Using fallback mode.");
                this.visionClient = null;
                return;
            }
            
            this.visionClient = ImageAnnotatorClient.create();
            logger.info("Google Cloud Vision API client initialized successfully. Credentials: {}", credentialsPath);
        } catch (IOException e) {
            logger.error("Failed to initialize Google Cloud Vision API client. Error: {}. Using fallback mode.", e.getMessage());
            this.visionClient = null;
        } catch (Exception e) {
            logger.error("Unexpected error initializing Google Cloud Vision API client: {}. Using fallback mode.", e.getMessage());
            this.visionClient = null;
        }
    }

    public ImageAnalysisResult analyzeImage(byte[] imageBytes) {
        if (visionClient == null) {
            logger.warn("Vision API client not available, using fallback");
            return null;
        }

        String imageHash = calculateImageHash(imageBytes);
        
        ImageAnalysisResult cached = cache.get(imageHash);
        if (cached != null) {
            logger.debug("Returning cached analysis result");
            return cached;
        }

        int currentCount = requestCount.incrementAndGet();
        if (currentCount > FREE_TIER_LIMIT) {
            logger.warn("Free tier limit reached ({} requests). Using fallback.", FREE_TIER_LIMIT);
            return null;
        }

        try {
            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature labelFeature = Feature.newBuilder()
                    .setType(Feature.Type.LABEL_DETECTION)
                    .setMaxResults(10)
                    .build();

            Feature objectFeature = Feature.newBuilder()
                    .setType(Feature.Type.OBJECT_LOCALIZATION)
                    .setMaxResults(10)
                    .build();

            Feature colorFeature = Feature.newBuilder()
                    .setType(Feature.Type.IMAGE_PROPERTIES)
                    .build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(labelFeature)
                    .addFeatures(objectFeature)
                    .addFeatures(colorFeature)
                    .setImage(img)
                    .build();

            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(
                    List.of(request));

            AnnotateImageResponse imageResponse = response.getResponses(0);

            if (imageResponse.hasError()) {
                logger.error("Error analyzing image: {}", imageResponse.getError().getMessage());
                return null;
            }

            List<String> labels = new ArrayList<>();
            for (EntityAnnotation annotation : imageResponse.getLabelAnnotationsList()) {
                if (annotation.getScore() > 0.5) {
                    labels.add(annotation.getDescription().toLowerCase());
                }
            }

            List<String> objects = new ArrayList<>();
            for (LocalizedObjectAnnotation annotation : imageResponse.getLocalizedObjectAnnotationsList()) {
                if (annotation.getScore() > 0.5) {
                    objects.add(annotation.getName().toLowerCase());
                }
            }

            List<String> colors = new ArrayList<>();
            if (imageResponse.hasImagePropertiesAnnotation()) {
                DominantColorsAnnotation colorsAnnotation = 
                        imageResponse.getImagePropertiesAnnotation().getDominantColors();
                for (ColorInfo colorInfo : colorsAnnotation.getColorsList()) {
                    if (colorInfo.getScore() > 0.1) {
                        com.google.type.Color color = colorInfo.getColor();
                        int red = (int) color.getRed();
                        int green = (int) color.getGreen();
                        int blue = (int) color.getBlue();
                        String hexColor = String.format("#%02X%02X%02X", red, green, blue);
                        colors.add(hexColor);
                    }
                }
            }

            ImageAnalysisResult result = new ImageAnalysisResult(labels, colors, objects);
            cache.put(imageHash, result);
            
            logger.info("Image analyzed successfully. Labels: {}, Colors: {}, Objects: {}", 
                    labels.size(), colors.size(), objects.size());
            
            return result;

        } catch (Exception e) {
            logger.error("Error calling Vision API", e);
            return null;
        }
    }

    private String calculateImageHash(byte[] imageBytes) {
        return String.valueOf(imageBytes.length) + "_" + 
               String.valueOf(java.util.Arrays.hashCode(imageBytes));
    }

    public int getRemainingRequests() {
        return Math.max(0, FREE_TIER_LIMIT - requestCount.get());
    }

    public void resetRequestCount() {
        requestCount.set(0);
        logger.info("Request count reset");
    }

    public static class ImageAnalysisResult {
        private final List<String> labels;
        private final List<String> colors;
        private final List<String> objects;

        public ImageAnalysisResult(List<String> labels, List<String> colors, List<String> objects) {
            this.labels = labels;
            this.colors = colors;
            this.objects = objects;
        }

        public List<String> getLabels() {
            return labels;
        }

        public List<String> getColors() {
            return colors;
        }

        public List<String> getObjects() {
            return objects;
        }
    }
}


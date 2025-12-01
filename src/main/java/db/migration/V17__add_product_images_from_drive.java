package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class V17__add_product_images_from_drive extends BaseJavaMigration {

    private static final String DRIVE_FOLDER_ID = "1iFEdYk_vECajj_vk6hE-dxPTppE4xKYf";
    
    private static final Map<String, String> FILE_TO_PRODUCT_MAP = new HashMap<>();
    
    static {
        FILE_TO_PRODUCT_MAP.put("armario_de_cozinha_branco", "Armário de Cozinha Branco");
        FILE_TO_PRODUCT_MAP.put("cadeira_cozinha_moderna", "Cadeira de Cozinha Moderna");
        FILE_TO_PRODUCT_MAP.put("cama_box_casal_premium", "Cama Box Casal Premium");
        FILE_TO_PRODUCT_MAP.put("comoda_4_gavetas", "Cômoda 4 Gavetas");
        FILE_TO_PRODUCT_MAP.put("guarda_roupa_6_portas", "Guarda-Roupa 6 Portas");
        FILE_TO_PRODUCT_MAP.put("ilha_de_cozinha_com_bancada", "Ilha de Cozinha com Bancada");
        FILE_TO_PRODUCT_MAP.put("mesa_de_cabeceira_moderna", "Mesa de Cabeceira Moderna");
        FILE_TO_PRODUCT_MAP.put("mesa_de_centro_moderna", "Mesa de Centro Moderna");
        FILE_TO_PRODUCT_MAP.put("mesa_de_jantar_retangular", "Mesa de Jantar Retangular");
        FILE_TO_PRODUCT_MAP.put("poltrona_reclinavel_premium", "Poltrona Reclinável Premium");
        FILE_TO_PRODUCT_MAP.put("rack_para_tv_55_polegadas", "Rack para TV 55 polegadas");
        FILE_TO_PRODUCT_MAP.put("sofa_de_canto_premium", "Sofá de Canto Premium");
        FILE_TO_PRODUCT_MAP.put("sofa_retratil_3_lugares", "Sofá Retrátil 3 Lugares");
    }

    @Override
    public void migrate(Context context) throws Exception {
        Map<String, String> fileIdMap = extractFileIdsFromFolder();
        
        if (fileIdMap.isEmpty()) {
            fileIdMap = getManualFileIdMapping();
        }
        
        if (fileIdMap.isEmpty()) {
            return;
        }
        
        try (Statement statement = context.getConnection().createStatement()) {
            for (Map.Entry<String, String> entry : FILE_TO_PRODUCT_MAP.entrySet()) {
                String fileName = entry.getKey();
                String productName = entry.getValue();
                
                try {
                    String findProduct = "SELECT id FROM products WHERE name = ?";
                    try (PreparedStatement ps = context.getConnection().prepareStatement(findProduct)) {
                        ps.setString(1, productName);
                        ResultSet rs = ps.executeQuery();
                        
                        if (rs.next()) {
                            int productId = rs.getInt("id");
                            
                            String fileId = fileIdMap.get(fileName + ".jpg");
                            if (fileId == null) {
                                fileId = fileIdMap.get(fileName);
                            }
                            
                            if (fileId != null && !fileId.isEmpty()) {
                                String imageUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
                                byte[] imageBytes = downloadImage(imageUrl);
                                
                                if (imageBytes != null && imageBytes.length > 0) {
                                    String updateSql = "UPDATE products SET image = ? WHERE id = ?";
                                    try (PreparedStatement updatePs = context.getConnection().prepareStatement(updateSql)) {
                                        updatePs.setBytes(1, imageBytes);
                                        updatePs.setInt(2, productId);
                                        updatePs.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                    
                    Thread.sleep(300);
                } catch (Exception e) {
                    // Continua com próximo arquivo em caso de erro
                }
            }
        }
    }
    
    private Map<String, String> extractFileIdsFromFolder() {
        Map<String, String> fileIdMap = new HashMap<>();
        
        try {
            String folderUrl = "https://drive.google.com/drive/folders/" + DRIVE_FOLDER_ID;
            URL url = new URL(folderUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    
                    StringBuilder html = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        html.append(line).append("\n");
                    }
                    
                    String htmlContent = html.toString();
                    Pattern pattern = Pattern.compile("/file/d/([a-zA-Z0-9_-]{25,})/");
                    Matcher matcher = pattern.matcher(htmlContent);
                    
                    java.util.List<String> fileIds = new java.util.ArrayList<>();
                    while (matcher.find()) {
                        String fileId = matcher.group(1);
                        if (!fileIds.contains(fileId) && !fileId.equals(DRIVE_FOLDER_ID)) {
                            fileIds.add(fileId);
                        }
                    }
                    
                    int index = 0;
                    for (String fileId : fileIds) {
                        if (index < FILE_TO_PRODUCT_MAP.size()) {
                            String fileName = getFileNameFromFileId(fileId);
                            if (fileName != null) {
                                String normalizedName = fileName.toLowerCase()
                                    .replace(".jpg", "")
                                    .replace(".jpeg", "")
                                    .replace(".png", "")
                                    .replace(" ", "_")
                                    .replace("-", "_");
                                
                                for (String key : FILE_TO_PRODUCT_MAP.keySet()) {
                                    if (normalizedName.contains(key) || key.contains(normalizedName)) {
                                        fileIdMap.put(key + ".jpg", fileId);
                                        fileIdMap.put(key, fileId);
                                        break;
                                    }
                                }
                            } else {
                                if (index < FILE_TO_PRODUCT_MAP.size()) {
                                    String[] keys = FILE_TO_PRODUCT_MAP.keySet().toArray(new String[0]);
                                    if (index < keys.length) {
                                        fileIdMap.put(keys[index] + ".jpg", fileId);
                                        fileIdMap.put(keys[index], fileId);
                                    }
                                }
                            }
                            index++;
                        }
                    }
                }
            }
            
            connection.disconnect();
        } catch (Exception e) {
            // Ignora erros e retorna mapa vazio para usar fallback manual
        }
        
        return fileIdMap;
    }
    
    private String getFileNameFromFileId(String fileId) {
        try {
            String viewUrl = "https://drive.google.com/file/d/" + fileId + "/view";
            URL url = new URL(viewUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("title") || line.contains("fileName")) {
                            Pattern pattern = Pattern.compile("(armario_de_cozinha_branco|cadeira_cozinha_moderna|cama_box_casal_premium|" +
                                "comoda_4_gavetas|guarda_roupa_6_portas|ilha_de_cozinha_com_bancada|" +
                                "mesa_de_cabeceira_moderna|mesa_de_centro_moderna|mesa_de_jantar_retangular|" +
                                "poltrona_reclinavel_premium|rack_para_tv_55_polegadas|sofa_de_canto_premium|" +
                                "sofa_retratil_3_lugares)", Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                return matcher.group(1);
                            }
                        }
                    }
                }
            }
            connection.disconnect();
        } catch (Exception e) {
            // Ignora erros
        }
        return null;
    }
    
    private Map<String, String> getManualFileIdMapping() {
        Map<String, String> fileIdMap = new HashMap<>();
        
        fileIdMap.put("armario_de_cozinha_branco", "1mtNHOtwSvA0Y5Jk6PKW-Ff5YoJsfs3ct");
        fileIdMap.put("cadeira_cozinha_moderna", "13iN3GUwUa9Rs5N6FpNYcl6_Z1NoomL5H");
        fileIdMap.put("cama_box_casal_premium", "1z6M2qwtz5ewNwq7Nhaln5X34lysqBNrA");
        fileIdMap.put("comoda_4_gavetas", "15U36tL6w1q2rCgATc2PyLjHDX08bI1qg");
        fileIdMap.put("guarda_roupa_6_portas", "1yVktc3Nf0XTQWAfbAgLTWEMliBRhLQSh");
        fileIdMap.put("ilha_de_cozinha_com_bancada", "1-4cvO0N9HX29jVjW4QAWoyzoP8HCIGxN");
        fileIdMap.put("mesa_de_cabeceira_moderna", "1165nwCztLI_3vSukS0FlwcFJOSYm4vgl");
        fileIdMap.put("mesa_de_centro_moderna", "1HAI1yzEJp82DmHi_KMFhnQh-wwWp9RR3");
        fileIdMap.put("mesa_de_jantar_retangular", "1Oq11aYX76v8NIisUJCZto8a3WHjGA_DU");
        fileIdMap.put("poltrona_reclinavel_premium", "1aPJrXvKvzhvehxmw7ThNeiG1lnUaXvBV");
        fileIdMap.put("rack_para_tv_55_polegadas", "1yahxEXVTBkaWAHbo7eS3xymWMLTfIuM_");
        fileIdMap.put("sofa_de_canto_premium", "1IeyQwFd3AcPI754WrumXVM8CjfIGCZav");
        fileIdMap.put("sofa_retratil_3_lugares", "1PisPsr-UOmuE_KFF3Hz9tJ7Id_P__bkK");
        
        return fileIdMap;
    }
    
    private byte[] downloadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = connection.getInputStream()) {
                    return in.readAllBytes();
                }
            }
            
            connection.disconnect();
        } catch (Exception e) {
            // Ignora erros
        }
        
        return null;
    }
}


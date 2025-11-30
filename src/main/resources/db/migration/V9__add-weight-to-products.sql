-- Adiciona coluna weight_kg na tabela products
ALTER TABLE products
ADD COLUMN weight_kg DOUBLE PRECISION;

-- Atualiza produtos existentes com pesos estimados realistas
UPDATE products SET weight_kg = 25.0 WHERE name = 'Mesa de Jantar Retangular';
UPDATE products SET weight_kg = 8.0 WHERE name = 'Cadeira de Cozinha Moderna';
UPDATE products SET weight_kg = 35.0 WHERE name = 'Armário de Cozinha Branco';
UPDATE products SET weight_kg = 80.0 WHERE name = 'Ilha de Cozinha com Bancada';
UPDATE products SET weight_kg = 45.0 WHERE name = 'Sofá Retrátil 3 Lugares';
UPDATE products SET weight_kg = 25.0 WHERE name = 'Poltrona Reclinável Premium';
UPDATE products SET weight_kg = 12.0 WHERE name = 'Mesa de Centro Moderna';
UPDATE products SET weight_kg = 30.0 WHERE name = 'Rack para TV 55 polegadas';
UPDATE products SET weight_kg = 65.0 WHERE name = 'Sofá de Canto Premium';
UPDATE products SET weight_kg = 50.0 WHERE name = 'Cama Box Casal Premium';
UPDATE products SET weight_kg = 90.0 WHERE name = 'Guarda-Roupa 6 Portas';
UPDATE products SET weight_kg = 40.0 WHERE name = 'Cômoda 4 Gavetas';
UPDATE products SET weight_kg = 5.0 WHERE name = 'Mesa de Cabeceira Moderna';
UPDATE products SET weight_kg = 35.0 WHERE name = 'Mesa de Escritório Executiva';
UPDATE products SET weight_kg = 15.0 WHERE name = 'Cadeira Ergonômica Executiva';
UPDATE products SET weight_kg = 20.0 WHERE name = 'Estante para Livros';
UPDATE products SET weight_kg = 30.0 WHERE name = 'Mesa de Jantar Redonda Premium';
UPDATE products SET weight_kg = 10.0 WHERE name = 'Jogo de Cadeiras de Jantar';
UPDATE products SET weight_kg = 55.0 WHERE name = 'Buffet para Sala de Jantar';
UPDATE products SET weight_kg = 20.0 WHERE name = 'Mesa de Jantar Pequena';
UPDATE products SET weight_kg = 12.0 WHERE name = 'Cadeira de Escritório Básica';
UPDATE products SET weight_kg = 75.0 WHERE name = 'Sofá de Luxo Exclusivo';
UPDATE products SET weight_kg = 18.0 WHERE name = 'Mesa Antiga Descontinuada';

-- Define peso padrão para produtos que não foram atualizados (caso existam)
UPDATE products SET weight_kg = 15.0 WHERE weight_kg IS NULL;


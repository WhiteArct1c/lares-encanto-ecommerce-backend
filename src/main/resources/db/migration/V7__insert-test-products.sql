-- Script para popular banco de dados com produtos de teste
-- Este script insere produtos variados para permitir testes do sistema
-- Apenas produtos 1 ao 13

-- Inserir produtos de teste
-- Nota: Os preços de venda (sale_price) são calculados automaticamente pelo sistema
-- baseado no preço de custo + margem do grupo de precificação
-- Aqui estamos inserindo valores aproximados para facilitar os testes
-- Cores estão em formato hexadecimal

-- Produtos da categoria Cozinha (id=1)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Mesa de Jantar Retangular', 'Mesa de jantar retangular em madeira maciça, acomoda até 6 pessoas. Perfeita para refeições em família.', 800.00, 880.00, 'Mesa', '#8B4513', NULL, true, NOW(), NOW(), 1, 1),
('Cadeira de Cozinha Moderna', 'Cadeira ergonômica com encosto alto, estofada em tecido resistente. Ideal para cozinhas e áreas de jantar.', 150.00, 165.00, 'Cadeira', '#000000', NULL, true, NOW(), NOW(), 1, 1),
('Armário de Cozinha Branco', 'Armário suspenso com 3 portas e prateleiras internas. Perfeito para organização da cozinha.', 450.00, 495.00, 'Armário', '#FFFFFF', NULL, true, NOW(), NOW(), 1, 1),
('Ilha de Cozinha com Bancada', 'Ilha central com bancada em granito e gavetas espaçosas. Centro de preparo de alimentos.', 1200.00, 1320.00, 'Ilha', '#FFFFFF', NULL, true, NOW(), NOW(), 1, 2);

-- Produtos da categoria Sala de Estar (id=4)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Sofá Retrátil 3 Lugares', 'Sofá confortável com assento retrátil, estofado em tecido premium. Acomoda 3 pessoas confortavelmente.', 1200.00, 1320.00, 'Sofá', '#808080', NULL, true, NOW(), NOW(), 4, 1),
('Poltrona Reclinável Premium', 'Poltrona reclinável com apoio para pés, estofamento em couro sintético. Controle remoto incluído.', 800.00, 960.00, 'Poltrona', '#8B4513', NULL, true, NOW(), NOW(), 4, 2),
('Mesa de Centro Moderna', 'Mesa de centro com tampo em vidro temperado e base em metal. Design contemporâneo.', 350.00, 385.00, 'Mesa', '#000000', NULL, true, NOW(), NOW(), 4, 1),
('Rack para TV 55 polegadas', 'Rack moderno com espaço para TV até 55 polegadas, gavetas e prateleiras. Madeira MDF.', 600.00, 720.00, 'Rack', '#654321', NULL, true, NOW(), NOW(), 4, 2),
('Sofá de Canto Premium', 'Sofá de canto em L, estofado em veludo. Acomoda até 5 pessoas. Design elegante e confortável.', 2500.00, 3000.00, 'Sofá', '#0000FF', NULL, true, NOW(), NOW(), 4, 3);

-- Produtos da categoria Quarto (id=3)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Cama Box Casal Premium', 'Cama box casal com colchão incluso, cabeceira estofada. Base em madeira maciça.', 1500.00, 1800.00, 'Cama', '#FFFFFF', NULL, true, NOW(), NOW(), 3, 2),
('Guarda-Roupa 6 Portas', 'Guarda-roupa espaçoso com 6 portas, espelhos e gavetas. Organização completa para o quarto.', 1800.00, 2160.00, 'Guarda-Roupa', '#FFFFFF', NULL, true, NOW(), NOW(), 3, 2),
('Cômoda 4 Gavetas', 'Cômoda moderna com 4 gavetas espaçosas, puxadores em metal. Perfeita para organização.', 450.00, 495.00, 'Cômoda', '#8B4513', NULL, true, NOW(), NOW(), 3, 1),
('Mesa de Cabeceira Moderna', 'Mesa de cabeceira com gaveta e porta. Design minimalista, ideal para quartos modernos.', 200.00, 220.00, 'Mesa', '#000000', NULL, true, NOW(), NOW(), 3, 1);

-- Estoque dos produtos 1 ao 13
INSERT INTO stock (quantity, reserved_quantity, created_at, updated_at, product_id) VALUES
(50, 0, NOW(), NOW(), 1),  -- Mesa de Jantar Retangular
(30, 0, NOW(), NOW(), 2),  -- Cadeira de Cozinha Moderna
(20, 0, NOW(), NOW(), 3),  -- Armário de Cozinha Branco
(10, 0, NOW(), NOW(), 4),  -- Ilha de Cozinha
(25, 0, NOW(), NOW(), 5),  -- Sofá Retrátil
(15, 0, NOW(), NOW(), 6),  -- Poltrona Reclinável
(40, 0, NOW(), NOW(), 7),  -- Mesa de Centro
(12, 0, NOW(), NOW(), 8),  -- Rack para TV
(8, 0, NOW(), NOW(), 9),   -- Sofá de Canto Premium
(18, 0, NOW(), NOW(), 10), -- Cama Box Casal
(10, 0, NOW(), NOW(), 11), -- Guarda-Roupa
(35, 0, NOW(), NOW(), 12), -- Cômoda
(60, 0, NOW(), NOW(), 13); -- Mesa de Cabeceira

-- Resumo dos produtos inseridos:
-- Total: 13 produtos
-- - Cozinha (category_id=1): 4 produtos
-- - Sala de Estar (category_id=4): 5 produtos
-- - Quarto (category_id=3): 4 produtos
--
-- Grupos de precificação utilizados:
-- - Standard (pricing_groups_id=1): 7 produtos
-- - Premium (pricing_groups_id=2): 5 produtos
-- - Luxury (pricing_groups_id=3): 1 produto
--
-- Cores em hexadecimal:
-- - Marrom: #8B4513
-- - Preto: #000000
-- - Branco: #FFFFFF
-- - Cinza: #808080
-- - Marrom Escuro: #654321
-- - Azul: #0000FF


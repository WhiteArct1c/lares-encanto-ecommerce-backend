-- Script para popular banco de dados com produtos de teste
-- Este script insere produtos variados para permitir testes do sistema

-- Inserir produtos de teste
-- Nota: Os preços de venda (sale_price) são calculados automaticamente pelo sistema
-- baseado no preço de custo + margem do grupo de precificação
-- Aqui estamos inserindo valores aproximados para facilitar os testes

-- Produtos da categoria Cozinha (id=1)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Mesa de Jantar Retangular', 'Mesa de jantar retangular em madeira maciça, acomoda até 6 pessoas. Perfeita para refeições em família.', 800.00, 880.00, 'Mesa', 'Marrom', NULL, true, NOW(), NOW(), 1, 1),
('Cadeira de Cozinha Moderna', 'Cadeira ergonômica com encosto alto, estofada em tecido resistente. Ideal para cozinhas e áreas de jantar.', 150.00, 165.00, 'Cadeira', 'Preto', NULL, true, NOW(), NOW(), 1, 1),
('Armário de Cozinha Branco', 'Armário suspenso com 3 portas e prateleiras internas. Perfeito para organização da cozinha.', 450.00, 495.00, 'Armário', 'Branco', NULL, true, NOW(), NOW(), 1, 1),
('Ilha de Cozinha com Bancada', 'Ilha central com bancada em granito e gavetas espaçosas. Centro de preparo de alimentos.', 1200.00, 1320.00, 'Ilha', 'Branco', NULL, true, NOW(), NOW(), 1, 2);

-- Produtos da categoria Sala de Estar (id=4)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Sofá Retrátil 3 Lugares', 'Sofá confortável com assento retrátil, estofado em tecido premium. Acomoda 3 pessoas confortavelmente.', 1200.00, 1320.00, 'Sofá', 'Cinza', NULL, true, NOW(), NOW(), 4, 1),
('Poltrona Reclinável Premium', 'Poltrona reclinável com apoio para pés, estofamento em couro sintético. Controle remoto incluído.', 800.00, 960.00, 'Poltrona', 'Marrom', NULL, true, NOW(), NOW(), 4, 2),
('Mesa de Centro Moderna', 'Mesa de centro com tampo em vidro temperado e base em metal. Design contemporâneo.', 350.00, 385.00, 'Mesa', 'Preto', NULL, true, NOW(), NOW(), 4, 1),
('Rack para TV 55 polegadas', 'Rack moderno com espaço para TV até 55 polegadas, gavetas e prateleiras. Madeira MDF.', 600.00, 720.00, 'Rack', 'Marrom Escuro', NULL, true, NOW(), NOW(), 4, 2),
('Sofá de Canto Premium', 'Sofá de canto em L, estofado em veludo. Acomoda até 5 pessoas. Design elegante e confortável.', 2500.00, 3000.00, 'Sofá', 'Azul', NULL, true, NOW(), NOW(), 4, 3);

-- Produtos da categoria Quarto (id=3)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Cama Box Casal Premium', 'Cama box casal com colchão incluso, cabeceira estofada. Base em madeira maciça.', 1500.00, 1800.00, 'Cama', 'Branco', NULL, true, NOW(), NOW(), 3, 2),
('Guarda-Roupa 6 Portas', 'Guarda-roupa espaçoso com 6 portas, espelhos e gavetas. Organização completa para o quarto.', 1800.00, 2160.00, 'Guarda-Roupa', 'Branco', NULL, true, NOW(), NOW(), 3, 2),
('Cômoda 4 Gavetas', 'Cômoda moderna com 4 gavetas espaçosas, puxadores em metal. Perfeita para organização.', 450.00, 495.00, 'Cômoda', 'Marrom', NULL, true, NOW(), NOW(), 3, 1),
('Mesa de Cabeceira Moderna', 'Mesa de cabeceira com gaveta e porta. Design minimalista, ideal para quartos modernos.', 200.00, 220.00, 'Mesa', 'Preto', NULL, true, NOW(), NOW(), 3, 1);

-- Produtos da categoria Escritório (id=6)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Mesa de Escritório Executiva', 'Mesa de escritório com gavetas laterais e espaço para computador. Madeira nobre.', 900.00, 1080.00, 'Mesa', 'Marrom', NULL, true, NOW(), NOW(), 6, 2),
('Cadeira Ergonômica Executiva', 'Cadeira ergonômica com apoio lombar ajustável, rodízios e braços reguláveis. Conforto profissional.', 600.00, 720.00, 'Cadeira', 'Preto', NULL, true, NOW(), NOW(), 6, 2),
('Estante para Livros', 'Estante com 5 prateleiras, suporta até 200kg. Ideal para organização de livros e documentos.', 400.00, 440.00, 'Estante', 'Branco', NULL, true, NOW(), NOW(), 6, 1);

-- Produtos da categoria Sala de Jantar (id=5)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Mesa de Jantar Redonda Premium', 'Mesa redonda em madeira maciça, acomoda até 8 pessoas. Design clássico e elegante.', 1500.00, 1800.00, 'Mesa', 'Marrom', NULL, true, NOW(), NOW(), 5, 2),
('Jogo de Cadeiras de Jantar', 'Conjunto com 6 cadeiras estofadas, estilo clássico. Conforto e elegância para jantares.', 1200.00, 1320.00, 'Cadeira', 'Bege', NULL, true, NOW(), NOW(), 5, 1),
('Buffet para Sala de Jantar', 'Buffet espaçoso com portas e gavetas, ideal para guardar louças e talheres. Madeira nobre.', 1000.00, 1100.00, 'Buffet', 'Marrom Escuro', NULL, true, NOW(), NOW(), 5, 1);

-- Produtos com estoque variado para testes
-- Produtos com estoque alto (para testes normais)
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
(60, 0, NOW(), NOW(), 13), -- Mesa de Cabeceira
(20, 0, NOW(), NOW(), 14), -- Mesa de Escritório
(25, 0, NOW(), NOW(), 15), -- Cadeira Ergonômica
(30, 0, NOW(), NOW(), 16), -- Estante para Livros
(12, 0, NOW(), NOW(), 17), -- Mesa Redonda Premium
(24, 0, NOW(), NOW(), 18), -- Jogo de Cadeiras (6 unidades)
(15, 0, NOW(), NOW(), 19); -- Buffet

-- Produtos com estoque baixo (para testar validação de estoque)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Mesa de Jantar Pequena', 'Mesa compacta para 4 pessoas, ideal para apartamentos pequenos.', 500.00, 550.00, 'Mesa', 'Branco', NULL, true, NOW(), NOW(), 1, 1),
('Cadeira de Escritório Básica', 'Cadeira simples para escritório, confortável e acessível.', 200.00, 220.00, 'Cadeira', 'Preto', NULL, true, NOW(), NOW(), 6, 1);

INSERT INTO stock (quantity, reserved_quantity, created_at, updated_at, product_id) VALUES
(3, 0, NOW(), NOW(), 20),  -- Mesa de Jantar Pequena (estoque baixo)
(2, 0, NOW(), NOW(), 21);  -- Cadeira de Escritório Básica (estoque baixo)

-- Produto sem estoque (para testar validação)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Sofá de Luxo Exclusivo', 'Sofá premium em couro legítimo, edição limitada. Design exclusivo e sofisticado.', 5000.00, 6500.00, 'Sofá', 'Preto', NULL, true, NOW(), NOW(), 4, 3);

INSERT INTO stock (quantity, reserved_quantity, created_at, updated_at, product_id) VALUES
(0, 0, NOW(), NOW(), 22);  -- Sofá de Luxo (sem estoque)

-- Produto inativo (para testar validação de produto ativo)
INSERT INTO products (name, description, price, sale_price, type, color, image, is_active, created_at, updated_at, category_id, pricing_groups_id) VALUES
('Mesa Antiga Descontinuada', 'Mesa vintage descontinuada, não disponível para venda.', 300.00, 330.00, 'Mesa', 'Marrom', NULL, false, NOW(), NOW(), 1, 1);

INSERT INTO stock (quantity, reserved_quantity, created_at, updated_at, product_id) VALUES
(5, 0, NOW(), NOW(), 23);  -- Mesa Antiga (inativa)

-- Resumo dos produtos inseridos:
-- Total: 23 produtos
-- - 19 produtos ativos com estoque adequado
-- - 2 produtos com estoque baixo (para testar limites)
-- - 1 produto sem estoque (para testar validação)
-- - 1 produto inativo (para testar validação de produto ativo)
--
-- Categorias utilizadas:
-- - Cozinha: 5 produtos
-- - Sala de Estar: 5 produtos
-- - Quarto: 4 produtos
-- - Escritório: 3 produtos
-- - Sala de Jantar: 3 produtos
--
-- Grupos de precificação utilizados:
-- - Standard (10%): 12 produtos
-- - Premium (20%): 7 produtos
-- - Luxury (30%): 2 produtos


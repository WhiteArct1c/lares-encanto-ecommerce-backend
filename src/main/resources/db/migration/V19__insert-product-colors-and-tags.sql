-- Script para inserir cores e tags nos produtos de teste (produtos 1 ao 13)
-- Esta migração deve ser executada após V18__add-product-colors-and-tags.sql

-- Inserir cores na tabela colors (se não existirem)
INSERT INTO colors (hex_code, created_at, updated_at)
SELECT '#8B4513', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#8B4513')
UNION ALL
SELECT '#F1EFEF', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#F1EFEF')
UNION ALL
SELECT '#744318', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#744318')
UNION ALL
SELECT '#EBE8E8', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#EBE8E8')
UNION ALL
SELECT '#8B572A', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#8B572A')
UNION ALL
SELECT '#FFFFFF', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#FFFFFF')
UNION ALL
SELECT '#666565', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#666565')
UNION ALL
SELECT '#635347', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#635347')
UNION ALL
SELECT '#44250A', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#44250A')
UNION ALL
SELECT '#6D492C', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#6D492C')
UNION ALL
SELECT '#F5F5F5', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#F5F5F5')
UNION ALL
SELECT '#DD9051', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#DD9051')
UNION ALL
SELECT '#2F435A', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#2F435A')
UNION ALL
SELECT '#CCA786', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#CCA786')
UNION ALL
SELECT '#C99E76', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#C99E76')
UNION ALL
SELECT '#8D4E19', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM colors WHERE hex_code = '#8D4E19');

-- Inserir tags na tabela tags (se não existirem)
INSERT INTO tags (name, created_at, updated_at)
SELECT 'mesa', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'mesa')
UNION ALL
SELECT 'jantar', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'jantar')
UNION ALL
SELECT 'madeira', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'madeira')
UNION ALL
SELECT 'cadeira', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'cadeira')
UNION ALL
SELECT 'cozinha', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'cozinha')
UNION ALL
SELECT 'moderna', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'moderna')
UNION ALL
SELECT 'ergonomica', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'ergonomica')
UNION ALL
SELECT 'armario', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'armario')
UNION ALL
SELECT 'branco', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'branco')
UNION ALL
SELECT 'organizacao', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'organizacao')
UNION ALL
SELECT 'ilha', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'ilha')
UNION ALL
SELECT 'bancada', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'bancada')
UNION ALL
SELECT 'sofa', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'sofa')
UNION ALL
SELECT 'couch', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'couch')
UNION ALL
SELECT 'retratil', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'retratil')
UNION ALL
SELECT 'confortavel', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'confortavel')
UNION ALL
SELECT 'poltrona', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'poltrona')
UNION ALL
SELECT 'reclinavel', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'reclinavel')
UNION ALL
SELECT 'premium', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'premium')
UNION ALL
SELECT 'centro', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'centro')
UNION ALL
SELECT 'vidro', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'vidro')
UNION ALL
SELECT 'contemporaneo', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'contemporaneo')
UNION ALL
SELECT 'rack', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'rack')
UNION ALL
SELECT 'tv', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'tv')
UNION ALL
SELECT 'canto', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'canto')
UNION ALL
SELECT 'veludo', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'veludo')
UNION ALL
SELECT 'elegante', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'elegante')
UNION ALL
SELECT 'cama', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'cama')
UNION ALL
SELECT 'casal', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'casal')
UNION ALL
SELECT 'colchao', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'colchao')
UNION ALL
SELECT 'guarda-roupa', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'guarda-roupa')
UNION ALL
SELECT 'espelho', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'espelho')
UNION ALL
SELECT 'comoda', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'comoda')
UNION ALL
SELECT 'gavetas', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'gavetas')
UNION ALL
SELECT 'cabeceira', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'cabeceira')
UNION ALL
SELECT 'minimalista', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'minimalista')
UNION ALL
SELECT 'cinza', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'cinza')
UNION ALL
SELECT 'preto', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'preto')
UNION ALL
SELECT 'marrom', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'marrom')
UNION ALL
SELECT 'azul', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'azul')
UNION ALL
SELECT 'sala', NOW(), NOW() WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'sala');

-- Associar cores aos produtos (product_colors)
-- Produto 1: Mesa de Jantar Retangular
INSERT INTO product_colors (product_id, color_id)
SELECT 1, id FROM colors WHERE hex_code IN ('#8B4513', '#F1EFEF')
ON CONFLICT DO NOTHING;

-- Produto 2: Cadeira de Cozinha Moderna
INSERT INTO product_colors (product_id, color_id)
SELECT 2, id FROM colors WHERE hex_code IN ('#744318', '#EBE8E8')
ON CONFLICT DO NOTHING;

-- Produto 3: Armário de Cozinha Branco
INSERT INTO product_colors (product_id, color_id)
SELECT 3, id FROM colors WHERE hex_code IN ('#8B572A', '#FFFFFF')
ON CONFLICT DO NOTHING;

-- Produto 4: Ilha de Cozinha com Bancada
INSERT INTO product_colors (product_id, color_id)
SELECT 4, id FROM colors WHERE hex_code IN ('#666565', '#FFFFFF')
ON CONFLICT DO NOTHING;

-- Produto 5: Sofá Retrátil 3 Lugares
INSERT INTO product_colors (product_id, color_id)
SELECT 5, id FROM colors WHERE hex_code = '#635347'
ON CONFLICT DO NOTHING;

-- Produto 6: Poltrona Reclinável Premium
INSERT INTO product_colors (product_id, color_id)
SELECT 6, id FROM colors WHERE hex_code = '#44250A'
ON CONFLICT DO NOTHING;

-- Produto 7: Mesa de Centro Moderna
INSERT INTO product_colors (product_id, color_id)
SELECT 7, id FROM colors WHERE hex_code IN ('#6D492C', '#F5F5F5')
ON CONFLICT DO NOTHING;

-- Produto 8: Rack para TV 55 polegadas
INSERT INTO product_colors (product_id, color_id)
SELECT 8, id FROM colors WHERE hex_code = '#DD9051'
ON CONFLICT DO NOTHING;

-- Produto 9: Sofá de Canto Premium
INSERT INTO product_colors (product_id, color_id)
SELECT 9, id FROM colors WHERE hex_code = '#2F435A'
ON CONFLICT DO NOTHING;

-- Produto 10: Cama Box Casal Premium
INSERT INTO product_colors (product_id, color_id)
SELECT 10, id FROM colors WHERE hex_code IN ('#CCA786', '#FFFFFF')
ON CONFLICT DO NOTHING;

-- Produto 11: Guarda-Roupa 6 Portas
INSERT INTO product_colors (product_id, color_id)
SELECT 11, id FROM colors WHERE hex_code IN ('#8B572A', '#FFFFFF')
ON CONFLICT DO NOTHING;

-- Produto 12: Cômoda 4 Gavetas
INSERT INTO product_colors (product_id, color_id)
SELECT 12, id FROM colors WHERE hex_code IN ('#C99E76', '#FFFFFF')
ON CONFLICT DO NOTHING;

-- Produto 13: Mesa de Cabeceira Moderna
INSERT INTO product_colors (product_id, color_id)
SELECT 13, id FROM colors WHERE hex_code IN ('#8D4E19', '#FFFFFF')
ON CONFLICT DO NOTHING;

-- Associar tags aos produtos (product_tags)
-- Produto 1: Mesa de Jantar Retangular
INSERT INTO product_tags (product_id, tag_id)
SELECT 1, id FROM tags WHERE name IN ('jantar', 'madeira', 'marrom', 'mesa')
ON CONFLICT DO NOTHING;

-- Produto 2: Cadeira de Cozinha Moderna
INSERT INTO product_tags (product_id, tag_id)
SELECT 2, id FROM tags WHERE name IN ('branco', 'cadeira', 'cozinha', 'ergonomica', 'marrom', 'moderna')
ON CONFLICT DO NOTHING;

-- Produto 3: Armário de Cozinha Branco
INSERT INTO product_tags (product_id, tag_id)
SELECT 3, id FROM tags WHERE name IN ('armario', 'branco', 'cozinha', 'organizacao')
ON CONFLICT DO NOTHING;

-- Produto 4: Ilha de Cozinha com Bancada
INSERT INTO product_tags (product_id, tag_id)
SELECT 4, id FROM tags WHERE name IN ('bancada', 'branco', 'cinza', 'cozinha', 'ilha')
ON CONFLICT DO NOTHING;

-- Produto 5: Sofá Retrátil 3 Lugares
INSERT INTO product_tags (product_id, tag_id)
SELECT 5, id FROM tags WHERE name IN ('cinza', 'confortavel', 'couch', 'retratil', 'sofa')
ON CONFLICT DO NOTHING;

-- Produto 6: Poltrona Reclinável Premium
INSERT INTO product_tags (product_id, tag_id)
SELECT 6, id FROM tags WHERE name IN ('cadeira', 'confortavel', 'marrom', 'poltrona', 'premium', 'reclinavel')
ON CONFLICT DO NOTHING;

-- Produto 7: Mesa de Centro Moderna
INSERT INTO product_tags (product_id, tag_id)
SELECT 7, id FROM tags WHERE name IN ('centro', 'contemporaneo', 'mesa', 'sala', 'vidro')
ON CONFLICT DO NOTHING;

-- Produto 8: Rack para TV 55 polegadas
INSERT INTO product_tags (product_id, tag_id)
SELECT 8, id FROM tags WHERE name IN ('marrom', 'organizacao', 'rack', 'tv')
ON CONFLICT DO NOTHING;

-- Produto 9: Sofá de Canto Premium
INSERT INTO product_tags (product_id, tag_id)
SELECT 9, id FROM tags WHERE name IN ('azul', 'canto', 'couch', 'elegante', 'premium', 'sofa', 'veludo')
ON CONFLICT DO NOTHING;

-- Produto 10: Cama Box Casal Premium
INSERT INTO product_tags (product_id, tag_id)
SELECT 10, id FROM tags WHERE name IN ('branco', 'cama', 'casal', 'colchao', 'premium')
ON CONFLICT DO NOTHING;

-- Produto 11: Guarda-Roupa 6 Portas
INSERT INTO product_tags (product_id, tag_id)
SELECT 11, id FROM tags WHERE name IN ('branco', 'espelho', 'guarda-roupa', 'organizacao')
ON CONFLICT DO NOTHING;

-- Produto 12: Cômoda 4 Gavetas
INSERT INTO product_tags (product_id, tag_id)
SELECT 12, id FROM tags WHERE name IN ('comoda', 'gavetas', 'marrom', 'organizacao')
ON CONFLICT DO NOTHING;

-- Produto 13: Mesa de Cabeceira Moderna
INSERT INTO product_tags (product_id, tag_id)
SELECT 13, id FROM tags WHERE name IN ('cabeceira', 'mesa', 'minimalista', 'moderna')
ON CONFLICT DO NOTHING;


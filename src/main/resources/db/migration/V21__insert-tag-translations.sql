-- Script para inserir traduções em inglês das tags dos produtos
-- Este script deve ser executado após V19__insert-product-colors-and-tags.sql
-- e V20__create-tag-translations.sql
-- 
-- As traduções são necessárias para o sistema de busca por imagem funcionar corretamente,
-- pois o Google Vision API retorna labels em inglês, e precisamos fazer matching
-- com as tags dos produtos que estão em português.

-- Inserir traduções das tags (português -> inglês)
-- Usa ON CONFLICT para evitar duplicatas caso o script seja executado múltiplas vezes

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'table', NOW(), NOW()
FROM tags t
WHERE t.name = 'mesa'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'dinner', NOW(), NOW()
FROM tags t
WHERE t.name = 'jantar'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'wood', NOW(), NOW()
FROM tags t
WHERE t.name = 'madeira'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'chair', NOW(), NOW()
FROM tags t
WHERE t.name = 'cadeira'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'kitchen', NOW(), NOW()
FROM tags t
WHERE t.name = 'cozinha'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'modern', NOW(), NOW()
FROM tags t
WHERE t.name = 'moderna'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'ergonomic', NOW(), NOW()
FROM tags t
WHERE t.name = 'ergonomica'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'cabinet', NOW(), NOW()
FROM tags t
WHERE t.name = 'armario'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'white', NOW(), NOW()
FROM tags t
WHERE t.name = 'branco'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'organization', NOW(), NOW()
FROM tags t
WHERE t.name = 'organizacao'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'island', NOW(), NOW()
FROM tags t
WHERE t.name = 'ilha'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'countertop', NOW(), NOW()
FROM tags t
WHERE t.name = 'bancada'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'sofa', NOW(), NOW()
FROM tags t
WHERE t.name = 'sofa'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

-- 'couch' já está em inglês, mas vamos adicionar a tradução mesmo assim para consistência
INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'couch', NOW(), NOW()
FROM tags t
WHERE t.name = 'couch'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'retractable', NOW(), NOW()
FROM tags t
WHERE t.name = 'retratil'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'comfortable', NOW(), NOW()
FROM tags t
WHERE t.name = 'confortavel'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'armchair', NOW(), NOW()
FROM tags t
WHERE t.name = 'poltrona'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'reclining', NOW(), NOW()
FROM tags t
WHERE t.name = 'reclinavel'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

-- 'premium' já está em inglês, mas vamos adicionar a tradução mesmo assim para consistência
INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'premium', NOW(), NOW()
FROM tags t
WHERE t.name = 'premium'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'center', NOW(), NOW()
FROM tags t
WHERE t.name = 'centro'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'glass', NOW(), NOW()
FROM tags t
WHERE t.name = 'vidro'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'contemporary', NOW(), NOW()
FROM tags t
WHERE t.name = 'contemporaneo'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

-- 'rack' já está em inglês, mas vamos adicionar a tradução mesmo assim para consistência
INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'rack', NOW(), NOW()
FROM tags t
WHERE t.name = 'rack'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

-- 'tv' já está em inglês, mas vamos adicionar a tradução mesmo assim para consistência
INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'television', NOW(), NOW()
FROM tags t
WHERE t.name = 'tv'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'corner', NOW(), NOW()
FROM tags t
WHERE t.name = 'canto'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'velvet', NOW(), NOW()
FROM tags t
WHERE t.name = 'veludo'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'elegant', NOW(), NOW()
FROM tags t
WHERE t.name = 'elegante'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'bed', NOW(), NOW()
FROM tags t
WHERE t.name = 'cama'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'couple', NOW(), NOW()
FROM tags t
WHERE t.name = 'casal'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'mattress', NOW(), NOW()
FROM tags t
WHERE t.name = 'colchao'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'wardrobe', NOW(), NOW()
FROM tags t
WHERE t.name = 'guarda-roupa'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'mirror', NOW(), NOW()
FROM tags t
WHERE t.name = 'espelho'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'dresser', NOW(), NOW()
FROM tags t
WHERE t.name = 'comoda'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'drawers', NOW(), NOW()
FROM tags t
WHERE t.name = 'gavetas'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'bedside', NOW(), NOW()
FROM tags t
WHERE t.name = 'cabeceira'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'minimalist', NOW(), NOW()
FROM tags t
WHERE t.name = 'minimalista'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'gray', NOW(), NOW()
FROM tags t
WHERE t.name = 'cinza'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'black', NOW(), NOW()
FROM tags t
WHERE t.name = 'preto'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'brown', NOW(), NOW()
FROM tags t
WHERE t.name = 'marrom'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'blue', NOW(), NOW()
FROM tags t
WHERE t.name = 'azul'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;

INSERT INTO tag_translations (tag_id, language, translation, created_at, updated_at)
SELECT t.id, 'en', 'living room', NOW(), NOW()
FROM tags t
WHERE t.name = 'sala'
  AND NOT EXISTS (SELECT 1 FROM tag_translations tt WHERE tt.tag_id = t.id AND tt.language = 'en')
ON CONFLICT (tag_id, language) DO NOTHING;



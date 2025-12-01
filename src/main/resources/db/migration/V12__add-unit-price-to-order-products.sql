-- Migration para adicionar coluna unit_price em order_products
-- Este campo armazena o preço unitário pago na compra, necessário para calcular
-- corretamente o valor do cupom de troca

ALTER TABLE order_products
ADD COLUMN unit_price NUMERIC(10, 2);

-- Atualiza registros existentes com o preço atual do produto (salePrice)
-- Isso garante que pedidos antigos tenham um valor, mesmo que não seja o exato da compra
UPDATE order_products op
SET unit_price = p.sale_price
FROM products p
WHERE op.product_id = p.id AND op.unit_price IS NULL;

-- Torna a coluna obrigatória após popular dados existentes
ALTER TABLE order_products
ALTER COLUMN unit_price SET NOT NULL;


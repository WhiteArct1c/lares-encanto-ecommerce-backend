-- Migration para refatorar estrutura de exchanges
-- Uma Exchange agora representa uma solicitação completa com múltiplos itens
-- Cada item é armazenado em exchange_items

-- 1. Criar tabela exchange_items
CREATE TABLE exchange_items (
    id BIGSERIAL PRIMARY KEY,
    exchange_id BIGINT NOT NULL,
    order_product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    reason TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_exchange_item_exchange FOREIGN KEY (exchange_id) REFERENCES exchanges(id) ON DELETE CASCADE,
    CONSTRAINT fk_exchange_item_order_product FOREIGN KEY (order_product_id) REFERENCES order_products(id)
);

-- 2. Migrar dados existentes de exchanges para exchange_items
-- Para cada Exchange existente, cria um ExchangeItem correspondente
INSERT INTO exchange_items (exchange_id, order_product_id, quantity, reason, created_at, updated_at)
SELECT 
    id as exchange_id,
    order_product_id,
    quantity,
    reason,
    created_at,
    updated_at
FROM exchanges
WHERE order_product_id IS NOT NULL;

-- 3. Remover colunas que não são mais necessárias na tabela exchanges
-- (order_product_id, quantity, reason agora estão em exchange_items)
ALTER TABLE exchanges
DROP COLUMN IF EXISTS order_product_id,
DROP COLUMN IF EXISTS quantity,
DROP COLUMN IF EXISTS reason;

-- 4. Criar índices
CREATE INDEX idx_exchange_items_exchange_id ON exchange_items(exchange_id);
CREATE INDEX idx_exchange_items_order_product_id ON exchange_items(order_product_id);


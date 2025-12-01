-- Migration para criar tabelas de trocas/devoluções e cupons

-- Tabela de trocas/devoluções
CREATE TABLE exchanges (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    reason TEXT,
    status_id BIGINT NOT NULL,
    return_to_stock BOOLEAN NOT NULL DEFAULT FALSE,
    coupon_generated BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_exchange_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_exchange_order_product FOREIGN KEY (order_product_id) REFERENCES order_products(id),
    CONSTRAINT fk_exchange_status FOREIGN KEY (status_id) REFERENCES order_status(id)
);

-- Tabela de cupons
CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    value NUMERIC(10, 2) NOT NULL,
    used_value NUMERIC(10, 2) NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    customer_id BIGINT NOT NULL,
    exchange_id BIGINT,
    coupon_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_coupon_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_coupon_exchange FOREIGN KEY (exchange_id) REFERENCES exchanges(id),
    CONSTRAINT chk_coupon_type CHECK (coupon_type IN ('EXCHANGE', 'PROMOTIONAL'))
);

-- Índices para melhor performance
CREATE INDEX idx_exchanges_order_id ON exchanges(order_id);
CREATE INDEX idx_exchanges_status_id ON exchanges(status_id);
CREATE INDEX idx_coupons_customer_id ON coupons(customer_id);
CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_is_active ON coupons(is_active);


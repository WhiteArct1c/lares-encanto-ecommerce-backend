-- Migration para criar tabela order_coupons
-- Rastreia quais cupons foram usados em cada pedido

CREATE TABLE order_coupons (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    amount_used NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_order_coupon_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_coupon_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);

-- Índices para melhor performance
CREATE INDEX idx_order_coupons_order_id ON order_coupons(order_id);
CREATE INDEX idx_order_coupons_coupon_id ON order_coupons(coupon_id);


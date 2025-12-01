-- Insere cupons promocionais iniciais na base
-- Estes cupons são globais (customer_id = NULL) e podem ser usados por qualquer cliente

INSERT INTO coupons (
    code,
    value,
    used_value,
    is_active,
    expires_at,
    customer_id,
    exchange_id,
    coupon_type,
    max_uses
)
VALUES
    -- Cupom de R$ 50 com até 100 usos, expira em 6 meses
    ('PROMO50', 50.00, 0.00, TRUE, NOW() + INTERVAL '6 months', NULL, NULL, 'PROMOTIONAL', 100),
    -- Cupom de R$ 100 sem limite de usos (expira em 6 meses)
    ('PROMO100', 100.00, 0.00, TRUE, NOW() + INTERVAL '6 months', NULL, NULL, 'PROMOTIONAL', NULL);



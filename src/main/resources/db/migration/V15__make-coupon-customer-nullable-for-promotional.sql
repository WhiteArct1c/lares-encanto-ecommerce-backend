-- Migration para permitir cupons promocionais sem cliente específico
-- Cupons promocionais são válidos para qualquer cliente
-- Cupons de troca (EXCHANGE) continuam vinculados a um cliente específico

-- Tornar customer_id nullable
ALTER TABLE coupons
ALTER COLUMN customer_id DROP NOT NULL;

-- Adicionar constraint para garantir que cupons de troca sempre tenham cliente
-- Cupons promocionais podem ter customer_id null
ALTER TABLE coupons
ADD CONSTRAINT chk_coupon_customer 
CHECK (
    (coupon_type = 'EXCHANGE' AND customer_id IS NOT NULL) OR
    (coupon_type = 'PROMOTIONAL' AND (customer_id IS NULL OR customer_id IS NOT NULL))
);


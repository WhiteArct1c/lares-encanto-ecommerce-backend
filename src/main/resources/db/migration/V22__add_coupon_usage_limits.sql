-- Adiciona campos de controle de quantidade de usos para cupons promocionais

ALTER TABLE coupons
    ADD COLUMN IF NOT EXISTS max_uses INTEGER NULL,
    ADD COLUMN IF NOT EXISTS used_count INTEGER NOT NULL DEFAULT 0;



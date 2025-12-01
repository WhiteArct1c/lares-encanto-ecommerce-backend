-- Soft delete para endereços de cliente

ALTER TABLE address
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;



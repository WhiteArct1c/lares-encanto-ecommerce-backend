ALTER TABLE products
    ADD COLUMN pricing_groups_id INTEGER NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT fk_products_pricing_groups
    FOREIGN KEY (pricing_groups_id)
    REFERENCES pricing_groups (id);

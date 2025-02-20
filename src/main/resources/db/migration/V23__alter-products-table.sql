ALTER TABLE products
    ADD COLUMN pricing_group_id INTEGER NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT fk_products_pricing_group
    FOREIGN KEY (pricing_group_id)
    REFERENCES pricing_group (id);

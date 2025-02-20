CREATE TABLE product_status_history (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    previous_status BOOLEAN NOT NULL, -- Estado anterior (true = ativo, false = inativo)
    new_status BOOLEAN NOT NULL, -- Estado novo (true = ativo, false = inativo)
    reason TEXT, -- Motivo da alteração
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

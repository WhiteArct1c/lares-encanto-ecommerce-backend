CREATE TABLE product_categories (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL,
                          color VARCHAR(50),
                          is_active BOOLEAN NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          category_id INTEGER NOT NULL,
                          CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

CREATE TABLE stock (
                       id SERIAL PRIMARY KEY,
                       quantity INTEGER NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       product_id INTEGER NOT NULL UNIQUE,
                       CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);
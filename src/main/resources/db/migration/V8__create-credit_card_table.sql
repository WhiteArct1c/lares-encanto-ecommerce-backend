CREATE TABLE credit_card (
    id SERIAL PRIMARY KEY,
    card_flag VARCHAR(100) NOT NULL,
    card_name VARCHAR(100) NOT NULL,
    card_number VARCHAR(100) NOT NULL,
    card_code VARCHAR(10) NOT NULL,
    main_card BOOLEAN DEFAULT TRUE,
    customer_id INT REFERENCES customer(id)
);
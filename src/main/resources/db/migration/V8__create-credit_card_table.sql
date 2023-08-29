CREATE TABLE credit_card (
    id SERIAL PRIMARY KEY,
    card_owner VARCHAR(100) NOT NULL,
    card_number VARCHAR(100) NOT NULL,
    month_expiration VARCHAR(50) NOT NULL,
    year_expiration VARCHAR(50) NOT NULL,
    card_code VARCHAR(10) NOT NULL,
    customer_id INT UNIQUE REFERENCES customer(id)
);
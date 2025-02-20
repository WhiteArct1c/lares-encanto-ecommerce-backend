CREATE TABLE pricing_groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    profit_margin DECIMAL(5,2) NOT NULL CHECK (profit_margin >= 0)
);
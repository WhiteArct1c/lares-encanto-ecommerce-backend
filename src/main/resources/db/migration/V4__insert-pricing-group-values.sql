INSERT INTO pricing_groups (name, profit_margin, created_at, updated_at) VALUES
    ('Standard', 10.00, NOW(), NOW()), -- Margem de lucro de 10%.
    ('Premium', 20.00, NOW(), NOW()), --  Margem de lucro de 20%, para produtos mais sofisticados.
    ('Luxury', 30.00, NOW(), NOW()), -- Margem de 30%, para produtos de alto valor agregado.
    ('Wholesale', 5.00, NOW(), NOW()), -- Margem menor de 5%, usada para vendas em grande escala.
    ('Clearance', 2.50, NOW(), NOW()); -- Margem reduzida de 2,5%, para produtos em liquidação.

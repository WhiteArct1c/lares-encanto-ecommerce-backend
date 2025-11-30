-- Migration para adicionar o status ENTREGUE que estava faltando
-- Este status é necessário para o fluxo: EM TRANSPORTE -> ENTREGUE
INSERT INTO order_status (name, created_at, updated_at) 
SELECT 'ENTREGUE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE name = 'ENTREGUE');


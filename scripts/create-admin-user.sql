-- Script SQL para criar usuário administrador manualmente
-- Nome: Rodrigo Rocha
-- Email: admin@admin.com
-- Senha: Mat15766@
-- Role: ADMIN (0)

-- NOTA IMPORTANTE: Este script requer que você calcule o hash BCrypt da senha manualmente.
-- O script Flyway V16__create_admin_user.java calcula o hash automaticamente e é a forma recomendada.
-- 
-- Para usar este script SQL diretamente:
-- 1. Calcule o hash BCrypt de "Mat15766@" usando Java:
--    new BCryptPasswordEncoder().encode("Mat15766@")
-- 2. Substitua o valor 'REPLACE_WITH_BCRYPT_HASH' abaixo pelo hash calculado
-- 3. Execute este script no banco de dados

-- Verifica se o usuário já existe antes de inserir
INSERT INTO users (email, password, role, is_active)
SELECT 
    'admin@admin.com' as email,
    'REPLACE_WITH_BCRYPT_HASH' as password, -- SUBSTITUA pelo hash BCrypt de "Mat15766@"
    0 as role, -- 0 = ADMIN (ordinal do enum UserRole.ADMIN)
    '1' as is_active -- '1' = ativo
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@admin.com'
);


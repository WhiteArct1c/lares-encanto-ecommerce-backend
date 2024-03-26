-- Criação da função para deletar todos os registros de todas as tabelas, exceto gender e flyway_schema_history
CREATE OR REPLACE FUNCTION delete_all_records() RETURNS VOID AS $$
DECLARE
cur_table_name RECORD;
    tables_to_preserve TEXT[] := ARRAY['gender', 'flyway_schema_history'];
BEGIN
    -- Seleciona o nome de todas as tabelas no schema public
FOR cur_table_name IN
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
    LOOP
        -- Verifica se a tabela não está na lista de tabelas a serem preservadas
        IF cur_table_name.table_name NOT IN (SELECT unnest(tables_to_preserve)) THEN
            -- Executa a instrução DELETE para a tabela atual
            EXECUTE 'DELETE FROM ' || quote_ident(cur_table_name.table_name);
END IF;
END LOOP;
END;
$$ LANGUAGE plpgsql;

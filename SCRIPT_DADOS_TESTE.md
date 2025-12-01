# Script SQL de Dados de Teste

## Arquivo

`src/main/resources/db/migration/V7__insert-test-products.sql`

## Descrição

Este script SQL popula o banco de dados com **23 produtos de teste** variados, incluindo móveis de diferentes categorias, preços e níveis de estoque. Foi criado para facilitar testes do sistema de compras e validações.

---

## Produtos Inseridos

### 📊 Resumo Geral

- **Total de produtos:** 23
- **Produtos ativos com estoque:** 19
- **Produtos com estoque baixo:** 2 (para testar limites)
- **Produtos sem estoque:** 1 (para testar validação)
- **Produtos inativos:** 1 (para testar validação de produto ativo)

### 🏷️ Distribuição por Categoria

#### Cozinha (5 produtos)

1. Mesa de Jantar Retangular - R$ 880,00 - Estoque: 50
2. Cadeira de Cozinha Moderna - R$ 165,00 - Estoque: 30
3. Armário de Cozinha Branco - R$ 495,00 - Estoque: 20
4. Ilha de Cozinha com Bancada - R$ 1.320,00 - Estoque: 10
5. Mesa de Jantar Pequena - R$ 550,00 - Estoque: 3 ⚠️ (baixo)

#### Sala de Estar (5 produtos)

6. Sofá Retrátil 3 Lugares - R$ 1.320,00 - Estoque: 25
7. Poltrona Reclinável Premium - R$ 960,00 - Estoque: 15
8. Mesa de Centro Moderna - R$ 385,00 - Estoque: 40
9. Rack para TV 55 polegadas - R$ 720,00 - Estoque: 12
10. Sofá de Canto Premium - R$ 3.000,00 - Estoque: 8
11. Sofá de Luxo Exclusivo - R$ 6.500,00 - Estoque: 0 ❌ (sem estoque)

#### Quarto (4 produtos)

12. Cama Box Casal Premium - R$ 1.800,00 - Estoque: 18
13. Guarda-Roupa 6 Portas - R$ 2.160,00 - Estoque: 10
14. Cômoda 4 Gavetas - R$ 495,00 - Estoque: 35
15. Mesa de Cabeceira Moderna - R$ 220,00 - Estoque: 60

#### Escritório (3 produtos)

16. Mesa de Escritório Executiva - R$ 1.080,00 - Estoque: 20
17. Cadeira Ergonômica Executiva - R$ 720,00 - Estoque: 25
18. Estante para Livros - R$ 440,00 - Estoque: 30
19. Cadeira de Escritório Básica - R$ 220,00 - Estoque: 2 ⚠️ (baixo)

#### Sala de Jantar (3 produtos)

20. Mesa de Jantar Redonda Premium - R$ 1.800,00 - Estoque: 12
21. Jogo de Cadeiras de Jantar - R$ 1.320,00 - Estoque: 24
22. Buffet para Sala de Jantar - R$ 1.100,00 - Estoque: 15

#### Produto Inativo (para testes)

23. Mesa Antiga Descontinuada - R$ 330,00 - Estoque: 5 - **INATIVO** ⛔

---

## Grupos de Precificação Utilizados

| Grupo          | Margem | Produtos    |
| -------------- | ------ | ----------- |
| Standard (10%) | 10%    | 12 produtos |
| Premium (20%)  | 20%    | 7 produtos  |
| Luxury (30%)   | 30%    | 2 produtos  |

---

## Casos de Teste Cobertos

### ✅ Testes de Compra Normal

- Produtos com estoque adequado (19 produtos)
- Diferentes faixas de preço (R$ 165,00 a R$ 6.500,00)
- Múltiplas categorias

### ⚠️ Testes de Validação de Estoque

- **Produto com estoque baixo:**

  - Mesa de Jantar Pequena (ID: 20) - Estoque: 3
  - Cadeira de Escritório Básica (ID: 21) - Estoque: 2
  - **Teste:** Tentar comprar quantidade maior que disponível

- **Produto sem estoque:**
  - Sofá de Luxo Exclusivo (ID: 22) - Estoque: 0
  - **Teste:** Tentar comprar produto sem estoque

### ⛔ Testes de Produto Inativo

- Mesa Antiga Descontinuada (ID: 23) - Inativo
- **Teste:** Tentar comprar produto inativo

### 💳 Testes de Pagamento

- Produtos com valores variados para testar:
  - Valor mínimo por cartão (R$ 10,00)
  - Múltiplos cartões
  - Soma de pagamentos

---

## Como Executar

### Opção 1: Via Flyway (Automático)

O script será executado automaticamente quando você iniciar a aplicação, pois está na pasta `db/migration` com o padrão `V7__`.

### Opção 2: Manualmente

```sql
-- Conecte-se ao banco de dados PostgreSQL
psql -U postgres -d postgres

-- Execute o script
\i src/main/resources/db/migration/V7__insert-test-products.sql
```

### Opção 3: Via Docker

```bash
# Se estiver usando docker-compose
docker exec -i db psql -U postgres -d postgres < src/main/resources/db/migration/V7__insert-test-products.sql
```

---

## Verificação

Após executar o script, você pode verificar os dados inseridos:

```sql
-- Contar produtos
SELECT COUNT(*) FROM products;

-- Ver produtos ativos com estoque
SELECT p.id, p.name, p.sale_price, s.quantity
FROM products p
JOIN stock s ON p.id = s.product_id
WHERE p.is_active = true AND s.quantity > 0
ORDER BY p.name;

-- Ver produtos sem estoque
SELECT p.id, p.name, s.quantity
FROM products p
JOIN stock s ON p.id = s.product_id
WHERE s.quantity = 0;

-- Ver produtos inativos
SELECT p.id, p.name, p.is_active
FROM products p
WHERE p.is_active = false;
```

---

## Exemplos de Testes

### Teste 1: Compra Normal

```
Produto: Mesa de Jantar Retangular (ID: 1)
Quantidade: 2
Valor: R$ 1.760,00
Cartão: R$ 1.760,00 (1x)
Resultado esperado: ✅ Pedido criado com sucesso
```

### Teste 2: Estoque Insuficiente

```
Produto: Mesa de Jantar Pequena (ID: 20)
Quantidade: 5 (estoque disponível: 3)
Resultado esperado: ❌ Erro 400: "Quantidade solicitada (5) para o produto Mesa de Jantar Pequena é maior que a disponível (3)"
```

### Teste 3: Produto Sem Estoque

```
Produto: Sofá de Luxo Exclusivo (ID: 22)
Quantidade: 1
Resultado esperado: ❌ Erro 400: "Produto Sofá de Luxo Exclusivo está sem estoque disponível"
```

### Teste 4: Produto Inativo

```
Produto: Mesa Antiga Descontinuada (ID: 23)
Quantidade: 1
Resultado esperado: ❌ Erro 400: "Produto com ID 23 não encontrado ou indisponível"
```

### Teste 5: Valor Mínimo de Cartão

```
Produto: Mesa de Cabeceira Moderna (ID: 15)
Quantidade: 1
Valor: R$ 220,00
Cartão 1: R$ 5,00
Cartão 2: R$ 215,00
Resultado esperado: ❌ Erro 400: "O valor mínimo por cartão de crédito é R$ 10,00. Valor informado: R$ 5,00"
```

### Teste 6: Soma de Pagamentos Incorreta

```
Produto: Sofá Retrátil (ID: 6)
Quantidade: 1
Valor total: R$ 1.320,00
Cartão 1: R$ 1.000,00
Cartão 2: R$ 200,00
Soma: R$ 1.200,00
Resultado esperado: ❌ Erro 400: "A soma dos pagamentos (R$ 1.200,00) não confere com o valor total do pedido (R$ 1.320,00)"
```

---

## Notas Importantes

1. **Preços de Venda:** Os preços de venda (`sale_price`) no script são aproximados. O sistema calcula automaticamente baseado na margem do grupo de precificação, então podem haver pequenas diferenças.

2. **Imagens:** Todos os produtos foram inseridos sem imagem (`NULL`). Você pode adicionar imagens posteriormente via API.

3. **IDs Sequenciais:** Os IDs dos produtos serão gerados automaticamente pelo banco. Os IDs mencionados neste documento são estimativas baseadas na ordem de inserção.

4. **Estoque Reservado:** Todos os produtos começam com `reserved_quantity = 0`. Isso será atualizado automaticamente quando pedidos forem criados.

---

## Próximos Passos

Após executar o script, você pode:

1. ✅ Testar criação de pedidos com produtos variados
2. ✅ Testar validações de estoque
3. ✅ Testar validações de pagamento
4. ✅ Testar atualização de status de pedidos
5. ✅ Testar baixa e desbloqueio de estoque

---

## Manutenção

Se precisar adicionar mais produtos ou modificar os existentes, você pode:

1. Criar uma nova migration `V8__insert-more-products.sql`
2. Ou executar INSERTs diretamente no banco
3. Ou usar a API `POST /products` (requer autenticação ADMIN)

---

**Última atualização:** 2024

# Análise de Requisitos - Lares Encanto E-Commerce Backend

## Resumo Executivo

**Porcentagem de Conclusão Geral: ~42%**

### Distribuição por Módulo:
- **Módulo 1 - Gestão de Produtos**: ~35%
- **Módulo 2 - Gestão de Clientes**: ~65%
- **Módulo 3 - Vendas e Carrinho**: ~40%
- **Módulo 4 - Trocas e Devoluções**: ~15%
- **Módulo 5 - Controle de Estoque**: ~50%
- **Módulo 6 - Análise**: ~0%
- **Requisitos Gerais**: ~30%

---

## 1. MÓDULO: GESTÃO DE PRODUTOS (MÓVEIS)

### Requisitos Funcionais (RF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RF0011 | Cadastrar móvel | ✅ **IMPLEMENTADO** | Endpoint POST /products existe, mas faltam campos obrigatórios (RN0011) |
| RF0012 | Inativar cadastro de móvel | ✅ **IMPLEMENTADO** | Endpoint PATCH /products/disable com motivo |
| RF0013 | Inativar móvel automaticamente | ❌ **NÃO IMPLEMENTADO** | Não há lógica para inativação automática por estoque/vendas |
| RF0014 | Alterar cadastro de móvel | ✅ **IMPLEMENTADO** | Endpoint PUT /products/{id} |
| RF0015 | Consulta de móveis | ⚠️ **PARCIAL** | Existe GET /products, mas filtros são limitados (apenas paginação) |
| RF0016 | Ativar cadastro de móveis | ✅ **IMPLEMENTADO** | Endpoint PATCH /products/enable com motivo |

### Regras de Negócio (RN)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RN0011 | Dados obrigatórios | ❌ **NÃO IMPLEMENTADO** | Faltam: Designer/Fabricante, Ano, Marca, Coleção, SKU, Material Principal, Descrição Técnica, Dimensões, Código de Barras |
| RN0012 | Associação com categorias | ⚠️ **PARCIAL** | Produto tem apenas UMA categoria (ManyToOne), deveria ser ManyToMany |
| RN0013 | Definindo valor de venda | ✅ **IMPLEMENTADO** | Cálculo baseado em margem de lucro do grupo de precificação |
| RN0014 | Validar margem de lucro | ❌ **NÃO IMPLEMENTADO** | Não há validação de margem mínima nem autorização de gerente |
| RN0015 | Associar motivo de inativação | ✅ **IMPLEMENTADO** | ProductStatusHistory registra motivo |
| RN0016 | Associar motivo de inativação automática | ❌ **NÃO IMPLEMENTADO** | Não há inativação automática |
| RN0017 | Associar motivo de ativação | ✅ **IMPLEMENTADO** | ProductStatusHistory registra motivo |

### Requisitos Não Funcionais (RNF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RNF0021 | Código de móvel único | ✅ **IMPLEMENTADO** | ID auto-incremento |
| RNF0013 | Script de implantação de domínios | ✅ **IMPLEMENTADO** | Flyway migrations (V2-V6) inserem dados iniciais |

**Conclusão Módulo 1: ~35%** (6/17 requisitos totalmente implementados)

---

## 2. MÓDULO: GESTÃO DE CLIENTES

### Requisitos Funcionais (RF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RF0021 | Cadastrar cliente | ✅ **IMPLEMENTADO** | POST /auth/register |
| RF0022 | Alterar cliente | ✅ **IMPLEMENTADO** | PUT /customers |
| RF0023 | Inativar cadastro de cliente | ✅ **IMPLEMENTADO** | POST /auth/deactivate-account |
| RF0024 | Consulta de clientes | ⚠️ **PARCIAL** | GET /customers (apenas ADMIN), mas filtros limitados |
| RF0025 | Consulta de transações | ✅ **IMPLEMENTADO** | GET /orders retorna pedidos do cliente |
| RF0026 | Cadastro de endereços | ✅ **IMPLEMENTADO** | POST /address, múltiplos endereços com apelido (title) |
| RF0027 | Cadastro de cartões | ✅ **IMPLEMENTADO** | CRUD completo, cartão preferencial (mainCard) |
| RF0028 | Alteração apenas de senha | ❌ **NÃO IMPLEMENTADO** | Não há endpoint específico |
| RF0034 | Alteração apenas de endereços | ✅ **IMPLEMENTADO** | PUT /address e DELETE /address |

### Regras de Negócio (RN)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RN0021 | Endereço de cobrança obrigatório | ⚠️ **PARCIAL** | Endereços têm categorias (AddressCategory), mas não há validação obrigatória |
| RN0022 | Endereço de entrega obrigatório | ⚠️ **PARCIAL** | Mesma situação acima |
| RN0023 | Composição do registro de endereços | ✅ **IMPLEMENTADO** | Todos os campos presentes |
| RN0024 | Composição do registro de cartões | ✅ **IMPLEMENTADO** | Todos os campos presentes |
| RN0025 | Bandeiras permitidas | ✅ **IMPLEMENTADO** | Validação de bandeira (ValidateFlag) |
| RN0026 | Dados obrigatórios do cliente | ⚠️ **PARCIAL** | Faltam: Tipo de Telefone, DDD separado |
| RN0027 | Ranking de cliente | ✅ **IMPLEMENTADO** | Campo ranking existe (inicializado com "0") |

### Requisitos Não Funcionais (RNF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RNF0031 | Senha forte | ✅ **IMPLEMENTADO** | Validações: mínimo 8 caracteres, maiúsculas, minúsculas, especiais |
| RNF0032 | Confirmação de senha | ✅ **IMPLEMENTADO** | ValidatePasswordNotEqual |
| RNF0033 | Criptografia | ✅ **IMPLEMENTADO** | BCryptPasswordEncoder |
| RNF0035 | Código de cliente único | ✅ **IMPLEMENTADO** | ID auto-incremento |

**Conclusão Módulo 2: ~65%** (13/20 requisitos totalmente implementados)

---

## 3. MÓDULO: VENDAS E CARRINHO

### Requisitos Funcionais (RF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RF0031 | Gerenciar carrinho | ❌ **NÃO IMPLEMENTADO** | Não há entidade/service de carrinho |
| RF0032 | Definir quantidade | ❌ **NÃO IMPLEMENTADO** | Depende do carrinho |
| RF0033 | Realizar compra | ⚠️ **PARCIAL** | POST /orders cria pedido, mas sem carrinho |
| RF0034 | Calcular frete | ⚠️ **PARCIAL** | OrderShipment existe, mas cálculo não é automático |
| RF0035 | Selecionar endereço | ✅ **IMPLEMENTADO** | Endereço pode ser selecionado ou novo |
| RF0036 | Selecionar pagamento | ⚠️ **PARCIAL** | Múltiplos cartões suportados, mas não há cupons |
| RF0037 | Finalizar Compra | ✅ **IMPLEMENTADO** | Status inicial: EM PROCESSAMENTO |
| RF0038 | Despachar produtos | ❌ **NÃO IMPLEMENTADO** | Não há endpoint para mudar para EM TRÂNSITO |
| RF0039 | Produtos entregues | ❌ **NÃO IMPLEMENTADO** | Não há endpoint para mudar para ENTREGUE |

### Regras de Negócio (RN)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RN0031 | Estoque na adição | ⚠️ **PARCIAL** | Validação existe no buildOrderProduct, mas não no "carrinho" |
| RN0044 | Bloqueio temporário (Reserva) | ⚠️ **PARCIAL** | reservedQuantity existe, mas não há timeout/expiração |
| RN0032 | Validação na compra | ⚠️ **PARCIAL** | Validação básica existe, mas não notifica/atualiza |
| RN0033 | Cupom promocional | ❌ **NÃO IMPLEMENTADO** | Não há entidade/service de cupons |
| RN0034 | Múltiplos cartões | ✅ **IMPLEMENTADO** | OrderPayment suporta múltiplos |
| RN0035 | Cupons + Cartão | ❌ **NÃO IMPLEMENTADO** | Depende de cupons |
| RN0036 | Troco em Cupom | ❌ **NÃO IMPLEMENTADO** | Depende de cupons |
| RN0037 | Validação final | ⚠️ **PARCIAL** | Validação básica, mas não valida operadora |
| RN0038 | Status de Aprovação | ⚠️ **PARCIAL** | Status APROVADO/REPROVADO existem, mas não há lógica automática |
| RN0028 | Baixa efetiva | ⚠️ **PARCIAL** | Reserva existe, mas baixa real não está implementada |

### Requisitos Não Funcionais (RNF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RNF0042 | Itens removidos | ❌ **NÃO IMPLEMENTADO** | Depende do carrinho |
| RNF0045 | Desbloqueio | ⚠️ **PARCIAL** | Reserva existe, mas não há lógica de desbloqueio automático |

**Conclusão Módulo 3: ~40%** (4/20 requisitos totalmente implementados)

---

## 4. MÓDULO: TROCAS E DEVOLUÇÕES

### Requisitos Funcionais (RF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RF0040 | Solicitar troca | ❌ **NÃO IMPLEMENTADO** | Não há endpoint/controller |
| RF0041 | Autorizar trocas | ❌ **NÃO IMPLEMENTADO** | Não há endpoint/controller |
| RF0042 | Visualização de trocas | ❌ **NÃO IMPLEMENTADO** | Não há endpoint/controller |
| RF0043 | Confirmar recebimento | ❌ **NÃO IMPLEMENTADO** | Não há endpoint/controller |
| RF0044 | Gerar cupom de troca | ❌ **NÃO IMPLEMENTADO** | Não há entidade/service de cupons |

### Regras de Negócio (RN)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RN0041 | Status de solicitação | ⚠️ **PARCIAL** | Status TROCA SOLICITADA existe no banco, mas não há lógica |
| RN0042 | Status pós-recebimento | ⚠️ **PARCIAL** | Status TROCADO existe no banco, mas não há lógica |
| RN0043 | Pré-requisito | ❌ **NÃO IMPLEMENTADO** | Não há validação de status ENTREGUE |

### Requisitos Não Funcionais (RNF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RNF0046 | Notificação | ❌ **NÃO IMPLEMENTADO** | Não há sistema de notificações |

**Conclusão Módulo 4: ~15%** (0/8 requisitos totalmente implementados, apenas estrutura de status)

---

## 5. MÓDULO: CONTROLE DE ESTOQUE

### Requisitos Funcionais (RF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RF0051 | Entrada em estoque | ⚠️ **PARCIAL** | Estoque criado junto com produto, mas não há endpoint específico |
| RF0052 | Valor de venda | ✅ **IMPLEMENTADO** | Calculado base custo + margem |
| RF0053 | Baixa em estoque | ⚠️ **PARCIAL** | Reserva existe, mas baixa efetiva não está clara |
| RF0054 | Reentrada | ❌ **NÃO IMPLEMENTADO** | Não há endpoint para reentrada |

### Regras de Negócio (RN)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RN0051 | Dados de entrada | ⚠️ **PARCIAL** | Faltam: Custo, Fornecedor, Data de Entrada (tem created_at) |
| RN005x | Custos diferentes | ❌ **NÃO IMPLEMENTADO** | Não há histórico de custos |
| RN0061 | Quantidade mínima | ⚠️ **PARCIAL** | Validação básica, mas não impede zero |
| RN0062 | Custo obrigatório | ❌ **NÃO IMPLEMENTADO** | Não há campo de custo |

**Conclusão Módulo 5: ~50%** (1/8 requisitos totalmente implementados)

---

## 6. MÓDULO: ANÁLISE (SIMPLIFICADO)

### Requisitos Funcionais (RF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RF0055 | Histórico de vendas | ❌ **NÃO IMPLEMENTADO** | Não há endpoint/controller |

### Requisitos Não Funcionais (RNF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RNF0043 | Gráfico | ❌ **NÃO IMPLEMENTADO** | Não há endpoint/controller |

**Conclusão Módulo 6: ~0%** (0/2 requisitos implementados)

---

## 7. REQUISITOS GERAIS (INFRAESTRUTURA)

### Requisitos Não Funcionais (RNF)

| ID | Requisito | Status | Observações |
|---|---|---|---|
| RNF0011 | Tempo de resposta | ⚠️ **PARCIAL** | Não há monitoramento/configuração específica |
| RNF0012 | Log de transação | ⚠️ **PARCIAL** | Auditoria JPA (@CreatedDate, @LastModifiedDate) existe, mas não registra usuário/dados anteriores |

**Conclusão Requisitos Gerais: ~30%** (0/2 requisitos totalmente implementados)

---

## PRINCIPAIS GAPS IDENTIFICADOS

### 1. **Carrinho de Compras**
- ❌ Não existe entidade/service de carrinho
- ❌ Não há gerenciamento de itens no carrinho
- ❌ Não há timeout/expiração de reservas

### 2. **Campos de Produto Incompletos**
- ❌ Faltam: Designer/Fabricante, Ano, Marca, Coleção, SKU, Material Principal, Descrição Técnica, Dimensões, Código de Barras
- ⚠️ Associação com múltiplas categorias (atualmente apenas uma)

### 3. **Sistema de Cupons**
- ❌ Não existe entidade/service de cupons
- ❌ Não há cupons promocionais
- ❌ Não há cupons de troca

### 4. **Trocas e Devoluções**
- ❌ Não há endpoints/controllers
- ❌ Não há lógica de negócio implementada
- ⚠️ Apenas status no banco de dados

### 5. **Análise de Vendas**
- ❌ Não há endpoints de relatórios
- ❌ Não há consultas comparativas
- ❌ Não há gráficos

### 6. **Controle de Estoque Avançado**
- ❌ Não há endpoint específico para entrada
- ❌ Não há campo de custo
- ❌ Não há histórico de custos
- ❌ Não há reentrada de produtos

### 7. **Validações de Negócio**
- ❌ Inativação automática de produtos
- ❌ Validação de margem de lucro mínima
- ❌ Autorização de gerente para alterações
- ❌ Validação de operadora de cartão

---

## RECOMENDAÇÕES PRIORITÁRIAS

1. **Alta Prioridade:**
   - Implementar carrinho de compras completo
   - Adicionar campos obrigatórios de produto (RN0011)
   - Implementar sistema de cupons
   - Implementar endpoints de troca/devolução

2. **Média Prioridade:**
   - Melhorar consultas com filtros avançados
   - Implementar análise de vendas
   - Adicionar controle de estoque completo (custo, fornecedor)
   - Implementar inativação automática de produtos

3. **Baixa Prioridade:**
   - Sistema de notificações
   - Log detalhado de transações
   - Monitoramento de performance

---

## CONCLUSÃO

O sistema possui uma **base sólida** com:
- ✅ Autenticação e autorização funcionais
- ✅ CRUD básico de produtos, clientes, pedidos
- ✅ Estrutura de banco de dados bem definida
- ✅ Validações de senha e segurança

Porém, **faltam funcionalidades críticas** para um e-commerce completo:
- ❌ Carrinho de compras
- ❌ Sistema de cupons
- ❌ Trocas e devoluções
- ❌ Análise de vendas
- ❌ Campos completos de produto

**Progresso Geral: ~42% dos requisitos implementados**


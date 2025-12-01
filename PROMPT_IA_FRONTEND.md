# 🤖 Prompt para IA - Atualizar Frontend com Cores e Tags

## Contexto

O backend de e-commerce foi atualizado para suportar múltiplas cores e tags para produtos. O frontend precisa ser adaptado para incluir esses novos campos nos formulários de criação/edição e na exibição de produtos.

## Mudanças no Backend

### Novos Campos na API

**Resposta de produto (`GET /products`, `GET /products/{id}`):**
```json
{
  "id": 5,
  "name": "Sofá Retrátil 3 Lugares",
  "color": "#808080",  // ⚠️ DEPRECATED (manter compatibilidade)
  "colors": [          // ✅ NOVO
    {"id": 1, "hexCode": "#808080", "name": "Cinza"},
    {"id": 2, "hexCode": "#000000", "name": "Preto"}
  ],
  "tags": [            // ✅ NOVO
    {"id": 1, "name": "sofa"},
    {"id": 2, "name": "couch"},
    {"id": 3, "name": "moderno"}
  ]
}
```

**Criação de produto (`POST /products`):**
- Novo parâmetro: `colorHexCodes` (List<String>) - múltiplos valores
- Novo parâmetro: `tagNames` (List<String>) - múltiplos valores

**Atualização de produto (`PUT /products/{id}`):**
- Novo parâmetro: `colorHexCodes` (List<String>, opcional)
- Novo parâmetro: `tagNames` (List<String>, opcional)

## Tarefa

Atualizar o frontend para:

1. **Atualizar tipos/interfaces** para incluir `colors: Color[]` e `tags: Tag[]`
2. **Adicionar campos no formulário de criação**:
   - Input para adicionar cores (formato hex: #RRGGBB)
   - Input para adicionar tags
   - Lista visual das cores/tags adicionadas com opção de remover
3. **Adicionar campos no formulário de edição**:
   - Exibir cores/tags atuais
   - Permitir adicionar/remover cores/tags
4. **Atualizar exibição de produtos**:
   - Mostrar todas as cores (com preview visual)
   - Mostrar tags como badges/chips
5. **Atualizar requisições HTTP**:
   - Enviar `colorHexCodes` e `tagNames` como arrays no FormData
   - **IMPORTANTE**: No form-data, adicionar cada valor separadamente:
     ```javascript
     formData.append('colorHexCodes', '#808080');
     formData.append('colorHexCodes', '#000000');
     formData.append('tagNames', 'sofa');
     formData.append('tagNames', 'couch');
     ```

## Validações

- **Cores**: Formato `#RRGGBB` (7 caracteres, regex: `/^#[0-9A-F]{6}$/i`)
- **Tags**: Não podem estar vazias, normalizar para lowercase
- Não permitir duplicatas

## Compatibilidade

- Manter suporte ao campo `color` antigo (deprecated)
- Se `colors` estiver vazio, usar `color` como fallback

## Exemplo de Implementação

```typescript
// Tipos
interface Color {
  id: number;
  hexCode: string;
  name: string | null;
}

interface Tag {
  id: number;
  name: string;
}

interface Product {
  // ... campos existentes
  color: string;  // DEPRECATED
  colors: Color[];
  tags: Tag[];
}

// FormData
const formData = new FormData();
formData.append('name', product.name);
// ... outros campos

// Adicionar cores (uma por vez!)
product.colors.forEach(hexCode => {
  formData.append('colorHexCodes', hexCode);
});

// Adicionar tags (uma por vez!)
product.tags.forEach(tagName => {
  formData.append('tagNames', tagName);
});
```

## Checklist

- [ ] Atualizar interfaces TypeScript
- [ ] Adicionar seletor de cores no formulário
- [ ] Adicionar seletor de tags no formulário
- [ ] Atualizar requisições HTTP
- [ ] Atualizar exibição de produtos
- [ ] Implementar validações
- [ ] Manter compatibilidade com campo `color` antigo



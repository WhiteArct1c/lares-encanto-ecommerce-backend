# 🎨 Prompt para Frontend - Adaptação para Múltiplas Cores e Tags

## 📋 Contexto

O backend foi atualizado para suportar **múltiplas cores** e **tags/características** para produtos. Isso melhora significativamente a precisão da busca por imagem e permite uma descrição mais rica dos produtos.

## 🔄 Mudanças no Backend

### 1. **Novos Campos na Resposta de Produto**

A resposta de `GET /products` e `GET /products/{id}` agora inclui:

```json
{
  "id": 5,
  "name": "Sofá Retrátil 3 Lugares",
  "description": "...",
  "price": 1200.00,
  "salePrice": 1320.00,
  "color": "#808080",  // ⚠️ DEPRECATED - manter para compatibilidade
  "colors": [          // ✅ NOVO - Lista de cores
    {
      "id": 1,
      "hexCode": "#808080",
      "name": "Cinza"
    },
    {
      "id": 2,
      "hexCode": "#000000",
      "name": "Preto"
    }
  ],
  "tags": [            // ✅ NOVO - Lista de tags
    {
      "id": 1,
      "name": "sofa"
    },
    {
      "id": 2,
      "name": "couch"
    },
    {
      "id": 3,
      "name": "moderno"
    },
    {
      "id": 4,
      "name": "cinza"
    },
    {
      "id": 5,
      "name": "confortavel"
    }
  ],
  "type": "Sofá",
  "category": {...},
  "pricingGroup": {...},
  "stockQuantity": 25,
  "weightKg": 15.0
}
```

### 2. **Novos Campos na Criação de Produto**

O endpoint `POST /products` agora aceita:

**Parâmetros adicionais:**

- `colorHexCodes` (List<String>): Lista de códigos hexadecimais de cores
- `tagNames` (List<String>): Lista de nomes de tags

**Exemplo de requisição:**

```
POST /products
Content-Type: multipart/form-data

name: Sofá Retrátil 3 Lugares
description: Sofá confortável...
price: 1200.00
type: Sofá
categoryId: 4
pricingGroupId: 1
initialStockQuantity: 25
colorHexCodes: #808080
colorHexCodes: #000000
tagNames: sofa
tagNames: couch
tagNames: moderno
tagNames: cinza
tagNames: confortavel
```

### 3. **Novos Campos na Atualização de Produto**

O endpoint `PUT /products/{id}` agora aceita:

**Parâmetros adicionais:**

- `colorHexCodes` (List<String>, opcional): Lista de códigos hexadecimais de cores
- `tagNames` (List<String>, opcional): Lista de nomes de tags

**Exemplo de requisição:**

```
PUT /products/5
Content-Type: multipart/form-data

tagNames: sofa
tagNames: couch
tagNames: retratil
tagNames: premium
```

**Comportamento:**

- Se `colorHexCodes` for fornecido, substitui todas as cores do produto
- Se `tagNames` for fornecido, substitui todas as tags do produto
- Se não for fornecido, mantém as cores/tags existentes

## 🎯 Tarefas para o Frontend

### 1. **Atualizar Interface/Tipos TypeScript**

```typescript
// Antes
interface Product {
  id: number;
  name: string;
  color: string; // ⚠️ DEPRECATED
  // ...
}

// Depois
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
  id: number;
  name: string;
  color: string; // ⚠️ DEPRECATED - manter para compatibilidade
  colors: Color[]; // ✅ NOVO
  tags: Tag[]; // ✅ NOVO
  // ...
}
```

### 2. **Atualizar Formulário de Criação de Produto**

**Adicionar campos para cores:**

- Campo de input para adicionar códigos hexadecimais
- Validação de formato: `#RRGGBB` (7 caracteres)
- Lista de cores adicionadas (com opção de remover)
- Preview visual das cores

**Adicionar campos para tags:**

- Campo de input para adicionar tags
- Autocomplete/sugestões de tags existentes (opcional)
- Lista de tags adicionadas (com opção de remover)
- Validação: tags não podem estar vazias

**Exemplo de UI:**

```
┌─────────────────────────────────────┐
│ Cores do Produto                     │
│ ┌─────────────────────────────────┐ │
│ │ #808080  [X]  #000000  [X]      │ │
│ └─────────────────────────────────┘ │
│ [Adicionar Cor]                      │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Tags/Características                 │
│ ┌─────────────────────────────────┐ │
│ │ sofa  [X]  couch  [X]          │ │
│ │ moderno  [X]  cinza  [X]        │ │
│ └─────────────────────────────────┘ │
│ [Adicionar Tag]                      │
└─────────────────────────────────────┘
```

### 3. **Atualizar Formulário de Edição de Produto**

- Exibir cores atuais do produto
- Permitir adicionar/remover cores
- Exibir tags atuais do produto
- Permitir adicionar/remover tags
- Salvar alterações via `PUT /products/{id}`

### 4. **Atualizar Exibição de Produto**

**Onde exibir cores:**

- Lista de produtos: mostrar todas as cores (ou primeira cor como principal)
- Detalhes do produto: mostrar todas as cores com preview visual
- Card de produto: usar primeira cor como cor principal

**Onde exibir tags:**

- Detalhes do produto: mostrar tags como badges/chips
- Lista de produtos: opcional (pode ser muito poluído)

**Exemplo de exibição:**

```jsx
// Cores
<div className="colors">
  {product.colors.map(color => (
    <div
      key={color.id}
      style={{ backgroundColor: color.hexCode }}
      title={color.name || color.hexCode}
    />
  ))}
</div>

// Tags
<div className="tags">
  {product.tags.map(tag => (
    <span key={tag.id} className="tag-badge">
      {tag.name}
    </span>
  ))}
</div>
```

### 5. **Atualizar Requisições HTTP**

**Criação de produto:**

```typescript
const formData = new FormData();
formData.append("name", productData.name);
formData.append("description", productData.description);
formData.append("price", productData.price.toString());
formData.append("type", productData.type);
formData.append("categoryId", productData.categoryId.toString());
formData.append("pricingGroupId", productData.pricingGroupId.toString());
formData.append(
  "initialStockQuantity",
  productData.initialStockQuantity.toString()
);

// Adicionar cores
productData.colorHexCodes.forEach((hexCode) => {
  formData.append("colorHexCodes", hexCode);
});

// Adicionar tags
productData.tagNames.forEach((tagName) => {
  formData.append("tagNames", tagName);
});

// Imagem (se houver)
if (productData.image) {
  formData.append("image", productData.image);
}

await fetch("/products", {
  method: "POST",
  body: formData,
});
```

**Atualização de produto:**

```typescript
const formData = new FormData();

// Campos opcionais
if (productData.name) formData.append("name", productData.name);
if (productData.description)
  formData.append("description", productData.description);
// ... outros campos

// Atualizar cores (se fornecido)
if (productData.colorHexCodes) {
  productData.colorHexCodes.forEach((hexCode) => {
    formData.append("colorHexCodes", hexCode);
  });
}

// Atualizar tags (se fornecido)
if (productData.tagNames) {
  productData.tagNames.forEach((tagName) => {
    formData.append("tagNames", tagName);
  });
}

await fetch(`/products/${productId}`, {
  method: "PUT",
  body: formData,
});
```

### 6. **Validações no Frontend**

**Cores:**

- Formato: `#RRGGBB` (7 caracteres, começando com #)
- Validação regex: `/^#[0-9A-F]{6}$/i`
- Normalização: converter para maiúsculas automaticamente
- Não permitir cores duplicadas

**Tags:**

- Não podem estar vazias
- Normalização: converter para lowercase, trim
- Não permitir tags duplicadas
- Sugestão: máximo de 10-15 tags por produto

### 7. **Compatibilidade com Código Antigo**

**Manter suporte ao campo `color` antigo:**

- Se `colors` estiver vazio mas `color` existir, usar `color` como fallback
- Migração gradual: mostrar ambos durante transição

```typescript
// Função auxiliar para obter cores
function getProductColors(product: Product): string[] {
  if (product.colors && product.colors.length > 0) {
    return product.colors.map((c) => c.hexCode);
  }
  // Fallback para campo antigo
  if (product.color) {
    return [product.color];
  }
  return [];
}
```

## 🔧 Como Enviar Listas no FormData

**IMPORTANTE**: No `multipart/form-data`, para enviar múltiplos valores com o mesmo nome (como `colorHexCodes` e `tagNames`), você deve adicionar o mesmo campo múltiplas vezes:

### JavaScript/TypeScript

```typescript
const formData = new FormData();

// ✅ CORRETO: Adicionar cada valor separadamente
formData.append("colorHexCodes", "#808080");
formData.append("colorHexCodes", "#000000");
formData.append("tagNames", "sofa");
formData.append("tagNames", "couch");
formData.append("tagNames", "moderno");

// ❌ ERRADO: Não funciona assim
formData.append("colorHexCodes", ["#808080", "#000000"]); // Não funciona!
formData.append("tagNames", "sofa,couch,moderno"); // Backend receberá como string única
```

### React/Next.js

```tsx
const handleSubmit = async (colors: string[], tags: string[]) => {
  const formData = new FormData();

  // Adicionar cada cor individualmente
  colors.forEach((hexCode) => {
    formData.append("colorHexCodes", hexCode);
  });

  // Adicionar cada tag individualmente
  tags.forEach((tagName) => {
    formData.append("tagNames", tagName);
  });

  await fetch("/products", {
    method: "POST",
    body: formData,
  });
};
```

### Axios

```typescript
const formData = new FormData();
colors.forEach((color) => formData.append("colorHexCodes", color));
tags.forEach((tag) => formData.append("tagNames", tag));

await axios.post("/products", formData, {
  headers: {
    "Content-Type": "multipart/form-data",
  },
});
```

### Fetch API

```typescript
const formData = new FormData();

// Adicionar cores
["#808080", "#000000"].forEach((hex) => {
  formData.append("colorHexCodes", hex);
});

// Adicionar tags
["sofa", "couch", "moderno"].forEach((tag) => {
  formData.append("tagNames", tag);
});

fetch("/products", {
  method: "POST",
  body: formData,
});
```

## 📝 Exemplos de Implementação

### Exemplo 1: Componente de Seleção de Cores

```tsx
interface ColorPickerProps {
  colors: string[];
  onChange: (colors: string[]) => void;
}

function ColorPicker({ colors, onChange }: ColorPickerProps) {
  const [newColor, setNewColor] = useState("");

  const addColor = () => {
    const hexPattern = /^#[0-9A-F]{6}$/i;
    const normalized = newColor.trim().toUpperCase();
    const finalColor = normalized.startsWith("#")
      ? normalized
      : `#${normalized}`;

    if (hexPattern.test(finalColor) && !colors.includes(finalColor)) {
      onChange([...colors, finalColor]);
      setNewColor("");
    }
  };

  const removeColor = (colorToRemove: string) => {
    onChange(colors.filter((c) => c !== colorToRemove));
  };

  return (
    <div>
      <div className="color-list">
        {colors.map((color) => (
          <div key={color} className="color-item">
            <div className="color-preview" style={{ backgroundColor: color }} />
            <span>{color}</span>
            <button onClick={() => removeColor(color)}>×</button>
          </div>
        ))}
      </div>
      <div className="color-input">
        <input
          type="text"
          value={newColor}
          onChange={(e) => setNewColor(e.target.value)}
          placeholder="#RRGGBB"
          pattern="^#[0-9A-F]{6}$"
        />
        <button onClick={addColor}>Adicionar</button>
      </div>
    </div>
  );
}
```

### Exemplo 2: Componente de Seleção de Tags

```tsx
interface TagInputProps {
  tags: string[];
  onChange: (tags: string[]) => void;
  suggestions?: string[]; // Tags existentes no sistema (opcional)
}

function TagInput({ tags, onChange, suggestions = [] }: TagInputProps) {
  const [newTag, setNewTag] = useState("");
  const [showSuggestions, setShowSuggestions] = useState(false);

  const addTag = () => {
    const normalized = newTag.trim().toLowerCase();
    if (normalized && !tags.includes(normalized)) {
      onChange([...tags, normalized]);
      setNewTag("");
    }
  };

  const removeTag = (tagToRemove: string) => {
    onChange(tags.filter((t) => t !== tagToRemove));
  };

  const filteredSuggestions = suggestions.filter(
    (s) =>
      !tags.includes(s.toLowerCase()) &&
      s.toLowerCase().includes(newTag.toLowerCase())
  );

  return (
    <div>
      <div className="tag-list">
        {tags.map((tag) => (
          <span key={tag} className="tag-badge">
            {tag}
            <button onClick={() => removeTag(tag)}>×</button>
          </span>
        ))}
      </div>
      <div className="tag-input">
        <input
          type="text"
          value={newTag}
          onChange={(e) => setNewTag(e.target.value)}
          onKeyPress={(e) => e.key === "Enter" && addTag()}
          placeholder="Digite uma tag e pressione Enter"
        />
        <button onClick={addTag}>Adicionar</button>
        {showSuggestions && filteredSuggestions.length > 0 && (
          <div className="suggestions">
            {filteredSuggestions.map((suggestion) => (
              <button
                key={suggestion}
                onClick={() => {
                  setNewTag(suggestion);
                  addTag();
                }}
              >
                {suggestion}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
```

### Exemplo 3: Formulário Completo de Produto

```tsx
function ProductForm({ product, onSubmit }: ProductFormProps) {
  const [formData, setFormData] = useState({
    name: product?.name || "",
    description: product?.description || "",
    price: product?.price || 0,
    type: product?.type || "",
    colorHexCodes: product?.colors?.map((c) => c.hexCode) || [],
    tagNames: product?.tags?.map((t) => t.name) || [],
    // ... outros campos
  });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    const formDataToSend = new FormData();
    formDataToSend.append("name", formData.name);
    formDataToSend.append("description", formData.description);
    formDataToSend.append("price", formData.price.toString());
    formDataToSend.append("type", formData.type);
    // ... outros campos

    // Adicionar cores
    formData.colorHexCodes.forEach((hexCode) => {
      formDataToSend.append("colorHexCodes", hexCode);
    });

    // Adicionar tags
    formData.tagNames.forEach((tagName) => {
      formDataToSend.append("tagNames", tagName);
    });

    const url = product ? `/products/${product.id}` : "/products";
    const method = product ? "PUT" : "POST";

    await fetch(url, {
      method,
      body: formDataToSend,
    });

    onSubmit();
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Campos existentes */}
      <input
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
      />

      {/* Novo: Seletor de Cores */}
      <ColorPicker
        colors={formData.colorHexCodes}
        onChange={(colors) =>
          setFormData({ ...formData, colorHexCodes: colors })
        }
      />

      {/* Novo: Seletor de Tags */}
      <TagInput
        tags={formData.tagNames}
        onChange={(tags) => setFormData({ ...formData, tagNames: tags })}
      />

      <button type="submit">Salvar</button>
    </form>
  );
}
```

### Exemplo 4: Exibição de Produto com Cores e Tags

```tsx
function ProductCard({ product }: { product: Product }) {
  const colors =
    product.colors && product.colors.length > 0
      ? product.colors
      : product.color
      ? [{ hexCode: product.color, name: null }]
      : [];

  return (
    <div className="product-card">
      <h3>{product.name}</h3>

      {/* Cores */}
      <div className="product-colors">
        {colors.map((color) => (
          <div
            key={color.hexCode}
            className="color-dot"
            style={{ backgroundColor: color.hexCode }}
            title={color.name || color.hexCode}
          />
        ))}
      </div>

      {/* Tags */}
      <div className="product-tags">
        {product.tags?.map((tag) => (
          <span key={tag.id} className="tag">
            {tag.name}
          </span>
        ))}
      </div>

      {/* Resto do card */}
    </div>
  );
}
```

## 🔍 Endpoints Atualizados

### GET /products

**Resposta:** Inclui `colors` e `tags` em cada produto

### GET /products/{id}

**Resposta:** Inclui `colors` e `tags` do produto

### POST /products

**Novos parâmetros:**

- `colorHexCodes` (List<String>): Lista de códigos hexadecimais
- `tagNames` (List<String>): Lista de nomes de tags

**Exemplo:**

```javascript
const formData = new FormData();
formData.append("name", "Sofá Retrátil");
formData.append("colorHexCodes", "#808080");
formData.append("colorHexCodes", "#000000");
formData.append("tagNames", "sofa");
formData.append("tagNames", "couch");
```

### PUT /products/{id}

**Novos parâmetros (opcionais):**

- `colorHexCodes` (List<String>): Substitui todas as cores
- `tagNames` (List<String>): Substitui todas as tags

## ⚠️ Importante

1. **Compatibilidade**: O campo `color` antigo ainda existe mas está deprecated

   - Use `colors` quando disponível
   - Use `color` como fallback se `colors` estiver vazio

2. **Validação de Cores**:

   - Formato: `#RRGGBB` (7 caracteres)
   - Backend normaliza automaticamente (adiciona #, converte para maiúsculas)

3. **Validação de Tags**:

   - Backend normaliza automaticamente (lowercase, trim)
   - Tags duplicadas são ignoradas

4. **Performance**:
   - Cores e tags são carregadas junto com o produto
   - Não há necessidade de requisições adicionais

## 🎯 Checklist de Implementação

- [ ] Atualizar tipos/interfaces TypeScript
- [ ] Adicionar campo de cores no formulário de criação
- [ ] Adicionar campo de tags no formulário de criação
- [ ] Adicionar campo de cores no formulário de edição
- [ ] Adicionar campo de tags no formulário de edição
- [ ] Atualizar exibição de produto para mostrar cores
- [ ] Atualizar exibição de produto para mostrar tags
- [ ] Implementar validação de formato de cor (hex)
- [ ] Implementar validação de tags (não vazias)
- [ ] Atualizar requisições HTTP para incluir cores e tags
- [ ] Testar criação de produto com cores e tags
- [ ] Testar edição de produto com cores e tags
- [ ] Implementar fallback para campo `color` antigo
- [ ] Atualizar componentes de lista de produtos
- [ ] Atualizar componentes de detalhes de produto

## 🧪 Testando no Postman/Insomnia

### Criar Produto com Cores e Tags

**Método:** `POST`
**URL:** `http://localhost:8080/products`
**Body:** `form-data`

| Key                  | Type | Value                   |
| -------------------- | ---- | ----------------------- |
| name                 | Text | Sofá Retrátil 3 Lugares |
| description          | Text | Sofá confortável...     |
| price                | Text | 1200.00                 |
| type                 | Text | Sofá                    |
| categoryId           | Text | 4                       |
| pricingGroupId       | Text | 1                       |
| initialStockQuantity | Text | 25                      |
| colorHexCodes        | Text | #808080                 |
| colorHexCodes        | Text | #000000                 |
| tagNames             | Text | sofa                    |
| tagNames             | Text | couch                   |
| tagNames             | Text | moderno                 |

**Importante:** Adicione `colorHexCodes` e `tagNames` múltiplas vezes, uma para cada valor!

### Atualizar Tags de um Produto

**Método:** `PUT`
**URL:** `http://localhost:8080/products/5`
**Body:** `form-data`

| Key      | Type | Value    |
| -------- | ---- | -------- |
| tagNames | Text | sofa     |
| tagNames | Text | couch    |
| tagNames | Text | retratil |
| tagNames | Text | premium  |

## 📚 Estrutura de Dados

### Color

```typescript
interface Color {
  id: number;
  hexCode: string; // Formato: #RRGGBB
  name: string | null; // Nome opcional da cor
}
```

### Tag

```typescript
interface Tag {
  id: number;
  name: string; // Nome da tag (lowercase, normalizado)
}
```

### Product (Atualizado)

```typescript
interface Product {
  // ... campos existentes
  color: string; // ⚠️ DEPRECATED
  colors: Color[]; // ✅ NOVO
  tags: Tag[]; // ✅ NOVO
}
```

## 🚀 Benefícios para o Frontend

1. **Melhor UX**: Usuário pode ver todas as cores do produto
2. **Melhor Busca**: Tags melhoram resultados de busca
3. **Flexibilidade**: Admin pode adicionar características relevantes
4. **Escalabilidade**: Sistema se adapta a novos produtos automaticamente

## 📞 Suporte

Se houver dúvidas sobre a implementação, consulte:

- `RESUMO_IMPLEMENTACAO_CORES_TAGS.md` - Documentação técnica completa
- Endpoints da API para exemplos de requisição/resposta

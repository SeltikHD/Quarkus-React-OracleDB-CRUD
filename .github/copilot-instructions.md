# GitHub Copilot Instructions for Autoflex ERP

> **Language Constraint:** All responses, code, comments, and documentation MUST be in **English**.

This document provides guidelines for AI assistants (GitHub Copilot, Claude, etc.) working on the Autoflex ERP codebase. Following these instructions ensures code consistency, architectural integrity, and maintainability.

---

## 🎯 Core Principles

### 1. Certainty Before Code
- **Only provide code if you are 95% confident it is correct.**
- If requirements are ambiguous, **ask clarifying questions first.**
- When multiple valid approaches exist, briefly explain the trade-offs before proceeding.
- Never guess database schemas, API contracts, or business rules.

### 2. Explain Before Complex Logic
For algorithms or complex business logic (e.g., production calculations, inventory management):
1. **Describe the algorithm in plain English first.**
2. Outline the inputs, outputs, and edge cases.
3. Only then provide the implementation.

---

## 🏛️ Hexagonal Architecture Rules

### Domain Layer (`com.autoflex.domain`)

The domain layer is the **sacred core** of the application. It contains pure business logic with **ZERO framework dependencies**.

#### ✅ ALLOWED in Domain Objects:
```java
// Pure Java only
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
```

#### ❌ FORBIDDEN in Domain Objects:
```java
// JPA Annotations - NEVER use these in domain entities
import jakarta.persistence.*;  // ❌ FORBIDDEN

// JSON Annotations
import com.fasterxml.jackson.annotation.*; // ❌ FORBIDDEN

// CDI/Quarkus Annotations
import jakarta.inject.Inject;  // ❌ FORBIDDEN
import jakarta.enterprise.context.ApplicationScoped; // ❌ FORBIDDEN

// Validation Annotations (use programmatic validation instead)
import jakarta.validation.constraints.*; // ❌ FORBIDDEN in domain
```

#### Domain Entity Pattern:
```java
// ✅ CORRECT: Pure domain entity
public class Product {
    private final ProductId id;
    private String name;
    
    // Factory method for creation
    public static Product create(String name, BigDecimal price) {
        validateName(name);  // Programmatic validation
        return new Product(null, name, price);
    }
    
    // Factory method for reconstitution from persistence
    public static Product reconstitute(ProductId id, String name, BigDecimal price) {
        return new Product(id, name, price);
    }
    
    // Domain behavior methods
    public void updatePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }
    
    // Private validation methods
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}
```

### Infrastructure Layer Mapping

JPA entities live in `infrastructure.persistence.entity` and map to domain objects:

```java
// ✅ CORRECT: JPA entity in infrastructure layer
@Entity
@Table(name = "PRODUCTS")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "NAME", nullable = false)
    private String name;
    
    // Getters and setters for JPA
}
```

### Port Interfaces

**Input Ports** (use cases) define what the application can do:
```java
public interface ProductUseCase {
    Product createProduct(CreateProductCommand command);
    Product getProductById(ProductId id);
}
```

**Output Ports** (repositories) define what the domain needs from infrastructure:
```java
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(ProductId id);
}
```

---

## ⚛️ React/TypeScript Rules

### Strict TypeScript

This project uses **maximum TypeScript strictness**. Never use `any`.

#### ❌ FORBIDDEN Patterns:
```typescript
// Never use 'any'
const data: any = response;  // ❌

// Never use type assertions to bypass checks
const user = data as User;  // ❌ (without validation)

// Never ignore TypeScript errors
// @ts-ignore  // ❌
// @ts-expect-error  // ❌ (unless documenting a known issue)
```

#### ✅ REQUIRED Patterns:
```typescript
// Use proper type definitions
interface IProduct {
  id: number;
  name: string;
  unitPrice: number;
}

// Use explicit return types for functions
function calculateTotal(items: IProduct[]): number {
  return items.reduce((sum, item) => sum + item.unitPrice, 0);
}

// Use type guards for runtime validation
function isProduct(data: unknown): data is IProduct {
  return (
    typeof data === 'object' &&
    data !== null &&
    'id' in data &&
    'name' in data
  );
}
```

### Redux Toolkit Patterns

Always use **Redux Toolkit** patterns:

```typescript
// ✅ CORRECT: Using createSlice
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

export const fetchProducts = createAsyncThunk(
  'products/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      return await productApi.getAll();
    } catch (error) {
      return rejectWithValue('Failed to fetch products');
    }
  }
);

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearError: (state) => { state.error = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending, (state) => { state.loading = true; })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.items = action.payload;
        state.loading = false;
      });
  },
});
```

### Component Patterns

```typescript
// ✅ CORRECT: Functional component with proper typing
interface IProductCardProps {
  product: IProduct;
  onEdit: (id: number) => void;
}

function ProductCard({ product, onEdit }: IProductCardProps): JSX.Element {
  const handleClick = (): void => {
    onEdit(product.id);
  };

  return (
    <Card onClick={handleClick}>
      <Typography>{product.name}</Typography>
    </Card>
  );
}
```

---

## 🧪 Test-Driven Development (TDD)

### Encourage TDD Workflow

When implementing new features:

1. **Write the test first** (ask: "What should this feature do?")
2. **See it fail** (ensure the test is valid)
3. **Write minimal code to pass**
4. **Refactor** while keeping tests green

### Backend Test Structure

```java
@DisplayName("Product Use Case")
class ProductServiceTest {

    @Nested
    @DisplayName("When creating a product")
    class CreateProduct {
        
        @Test
        @DisplayName("should create product with valid data")
        void shouldCreateProduct() {
            // Given
            var command = new CreateProductCommand("Widget", "SKU-001", BigDecimal.TEN);
            
            // When
            var result = productService.createProduct(command);
            
            // Then
            assertThat(result.getName()).isEqualTo("Widget");
            verify(productRepository).save(any(Product.class));
        }
        
        @Test
        @DisplayName("should reject duplicate SKU")
        void shouldRejectDuplicateSku() {
            // Given
            when(productRepository.existsBySku("SKU-001")).thenReturn(true);
            
            // When/Then
            assertThatThrownBy(() -> productService.createProduct(command))
                .isInstanceOf(ProductSkuAlreadyExistsException.class);
        }
    }
}
```

### Frontend Test Structure (Cypress)

```typescript
describe('Product Management', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/v1/products', { fixture: 'products.json' });
    cy.visit('/products');
  });

  it('should display product list', () => {
    cy.get('[data-testid="product-list"]').should('be.visible');
    cy.get('[data-testid="product-card"]').should('have.length.greaterThan', 0);
  });

  it('should open create modal when clicking add button', () => {
    cy.get('[data-testid="add-product-btn"]').click();
    cy.get('[data-testid="product-form-modal"]').should('be.visible');
  });
});
```

---

## 📝 Code Style Guidelines

### Java Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `ProductService` |
| Interfaces | PascalCase | `ProductRepository` |
| Methods | camelCase | `findById()` |
| Constants | UPPER_SNAKE | `MAX_RETRY_COUNT` |
| Packages | lowercase | `com.autoflex.domain` |

### TypeScript Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Interfaces | `I` prefix + PascalCase | `IProduct` |
| Types | PascalCase | `ProductState` |
| Components | PascalCase | `ProductCard` |
| Hooks | `use` prefix | `useProducts` |
| Constants | UPPER_SNAKE | `API_BASE_URL` |

---

## 🚫 Common Anti-Patterns to Avoid

### Backend

```java
// ❌ Anemic domain model (logic in service, not entity)
public class ProductService {
    public void applyDiscount(Product p, BigDecimal discount) {
        p.setPrice(p.getPrice().subtract(discount));  // ❌ Logic should be in Product
    }
}

// ✅ Rich domain model
public class Product {
    public void applyDiscount(BigDecimal discount) {
        validateDiscount(discount);
        this.price = this.price.subtract(discount);
    }
}
```

### Frontend

```typescript
// ❌ Mixing business logic with UI components
function ProductCard({ product }) {
  const discountedPrice = product.price * 0.9;  // ❌ Business logic in component
  return <div>{discountedPrice}</div>;
}

// ✅ Separate concerns
function calculateDiscountedPrice(price: number, discount: number): number {
  return price * (1 - discount);
}

function ProductCard({ product, discount }) {
  const finalPrice = calculateDiscountedPrice(product.price, discount);
  return <div>{finalPrice}</div>;
}
```

---

## ⚡ Quick Reference

### When Asked to Create a New Feature:

1. Ask: "Which layer does this belong to?" (Domain/Application/Infrastructure)
2. For domain logic: Ensure NO framework annotations
3. Write tests first (or alongside the implementation)
4. Follow existing patterns in the codebase

### When Unsure About Requirements:

Ask questions like:
- "Should this validation happen in the domain entity or at the API layer?"
- "What should happen if this operation fails?"
- "Are there any edge cases I should consider?"

### When Reviewing Code:

Check for:
- [ ] Domain objects are annotation-free
- [ ] TypeScript has no `any` types
- [ ] All functions have explicit return types
- [ ] Tests exist for the new functionality
- [ ] Error handling is implemented
- [ ] Comments explain "why", not "what"

---

## 📚 Additional Resources

- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
- [Quarkus Guides](https://quarkus.io/guides/)
- [Material UI Documentation](https://mui.com/)

---

*Last updated: February 2026*

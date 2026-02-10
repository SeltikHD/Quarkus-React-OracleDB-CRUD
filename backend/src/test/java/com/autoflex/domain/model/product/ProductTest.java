package com.autoflex.domain.model.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for the Product domain entity.
 *
 * <p>These tests verify the domain's business rules and invariants
 * without any infrastructure dependencies (pure unit tests).
 */
@DisplayName("Product Domain Entity")
class ProductTest {

    @Nested
    @DisplayName("Product Creation")
    class ProductCreation {

        @Test
        @DisplayName("should create a valid product with all required fields")
        void shouldCreateValidProduct() {
            // Given
            String name = "Test Product";
            String description = "A test product description";
            String sku = "TEST-001";
            BigDecimal price = new BigDecimal("29.99");
            Integer stock = 100;

            // When
            Product product = Product.create(name, description, sku, price, stock);

            // Then
            assertThat(product.getName()).isEqualTo(name);
            assertThat(product.getDescription()).isEqualTo(description);
            assertThat(product.getSku()).isEqualTo("TEST-001"); // SKU should be uppercased
            assertThat(product.getUnitPrice()).isEqualByComparingTo(price);
            assertThat(product.getStockQuantity()).isEqualTo(stock);
            assertThat(product.isActive()).isTrue(); // New products are active by default
            assertThat(product.getId()).isNull(); // ID is null before persistence
            assertThat(product.getCreatedAt()).isNotNull();
            assertThat(product.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should uppercase SKU during creation")
        void shouldUppercaseSku() {
            // When
            Product product = Product.create(
                    "Product",
                    null,
                    "abc-123",
                    BigDecimal.TEN,
                    0
            );

            // Then
            assertThat(product.getSku()).isEqualTo("ABC-123");
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullName() {
            assertThatThrownBy(() -> Product.create(
                    null,
                    "Description",
                    "SKU-001",
                    BigDecimal.TEN,
                    0
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject empty name")
        void shouldRejectEmptyName() {
            assertThatThrownBy(() -> Product.create(
                    "   ",
                    "Description",
                    "SKU-001",
                    BigDecimal.TEN,
                    0
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject invalid SKU characters")
        void shouldRejectInvalidSku() {
            assertThatThrownBy(() -> Product.create(
                    "Product",
                    null,
                    "SKU@001",
                    BigDecimal.TEN,
                    0
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("SKU");
        }

        @Test
        @DisplayName("should reject negative price")
        void shouldRejectNegativePrice() {
            assertThatThrownBy(() -> Product.create(
                    "Product",
                    null,
                    "SKU-001",
                    new BigDecimal("-10.00"),
                    0
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("price");
        }

        @Test
        @DisplayName("should reject negative stock")
        void shouldRejectNegativeStock() {
            assertThatThrownBy(() -> Product.create(
                    "Product",
                    null,
                    "SKU-001",
                    BigDecimal.TEN,
                    -5
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("quantity");
        }
    }

    @Nested
    @DisplayName("Stock Management")
    class StockManagement {

        @Test
        @DisplayName("should increase stock correctly")
        void shouldIncreaseStock() {
            // Given
            Product product = Product.create("Product", null, "SKU-001", BigDecimal.TEN, 100);

            // When
            product.adjustStock(50);

            // Then
            assertThat(product.getStockQuantity()).isEqualTo(150);
        }

        @Test
        @DisplayName("should decrease stock correctly")
        void shouldDecreaseStock() {
            // Given
            Product product = Product.create("Product", null, "SKU-001", BigDecimal.TEN, 100);

            // When
            product.adjustStock(-30);

            // Then
            assertThat(product.getStockQuantity()).isEqualTo(70);
        }

        @Test
        @DisplayName("should reject stock reduction below zero")
        void shouldRejectStockBelowZero() {
            // Given
            Product product = Product.create("Product", null, "SKU-001", BigDecimal.TEN, 50);

            // When/Then
            assertThatThrownBy(() -> product.adjustStock(-51))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("below zero");
        }

        @Test
        @DisplayName("should correctly check sufficient stock")
        void shouldCheckSufficientStock() {
            // Given
            Product product = Product.create("Product", null, "SKU-001", BigDecimal.TEN, 100);

            // Then
            assertThat(product.hasSufficientStock(100)).isTrue();
            assertThat(product.hasSufficientStock(99)).isTrue();
            assertThat(product.hasSufficientStock(101)).isFalse();
        }
    }

    @Nested
    @DisplayName("Product Lifecycle")
    class ProductLifecycle {

        @Test
        @DisplayName("should deactivate product")
        void shouldDeactivateProduct() {
            // Given
            Product product = Product.create("Product", null, "SKU-001", BigDecimal.TEN, 0);
            assertThat(product.isActive()).isTrue();

            // When
            product.deactivate();

            // Then
            assertThat(product.isActive()).isFalse();
        }

        @Test
        @DisplayName("should reactivate product")
        void shouldReactivateProduct() {
            // Given
            Product product = Product.create("Product", null, "SKU-001", BigDecimal.TEN, 0);
            product.deactivate();
            assertThat(product.isActive()).isFalse();

            // When
            product.activate();

            // Then
            assertThat(product.isActive()).isTrue();
        }

        @Test
        @DisplayName("should update product information")
        void shouldUpdateProduct() {
            // Given
            Product product = Product.create("Original", "Original Desc", "ORIG-001", BigDecimal.TEN, 0);

            // When
            product.update("Updated", "Updated Desc", "UPDT-001", new BigDecimal("20.00"));

            // Then
            assertThat(product.getName()).isEqualTo("Updated");
            assertThat(product.getDescription()).isEqualTo("Updated Desc");
            assertThat(product.getSku()).isEqualTo("UPDT-001");
            assertThat(product.getUnitPrice()).isEqualByComparingTo(new BigDecimal("20.00"));
        }
    }
}

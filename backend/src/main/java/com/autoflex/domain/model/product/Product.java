package com.autoflex.domain.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Product - Domain Entity representing a product in the Autoflex ERP system.
 *
 * <p>This is a pure domain entity with NO framework dependencies.
 * It contains only business logic and domain validation.
 *
 * <p><b>IMPORTANT:</b> This class must NOT contain:
 * <ul>
 *   <li>JPA annotations (@Entity, @Id, @Column)</li>
 *   <li>JSON annotations (@JsonProperty, @JsonIgnore)</li>
 *   <li>Any framework-specific code</li>
 * </ul>
 *
 * <p>The infrastructure layer (adapters) will handle the mapping
 * between this domain entity and persistence/API representations.
 */
public class Product {

    private final ProductId id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Private constructor - use factory methods or Builder.
     */
    private Product(
            ProductId id,
            String name,
            String description,
            String sku,
            BigDecimal unitPrice,
            Integer stockQuantity,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method for creating a new Product (without ID).
     * Use this when creating a new product that hasn't been persisted yet.
     */
    public static Product create(
            String name,
            String description,
            String sku,
            BigDecimal unitPrice,
            Integer stockQuantity) {
        
        validateName(name);
        validateSku(sku);
        validateUnitPrice(unitPrice);
        validateStockQuantity(stockQuantity);

        LocalDateTime now = LocalDateTime.now();
        return new Product(
                null,
                name.trim(),
                description != null ? description.trim() : null,
                sku.toUpperCase().trim(),
                unitPrice,
                stockQuantity,
                true, // new products are active by default
                now,
                now
        );
    }

    /**
     * Factory method for reconstituting a Product from persistence.
     * Use this in the repository adapter when loading from database.
     */
    public static Product reconstitute(
            ProductId id,
            String name,
            String description,
            String sku,
            BigDecimal unitPrice,
            Integer stockQuantity,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        
        return new Product(id, name, description, sku, unitPrice, stockQuantity, active, createdAt, updatedAt);
    }

    // =========================================================================
    // DOMAIN BEHAVIOR
    // =========================================================================

    /**
     * Updates the product information.
     * This method encapsulates the business rules for updating a product.
     */
    public void update(String name, String description, String sku, BigDecimal unitPrice) {
        validateName(name);
        validateSku(sku);
        validateUnitPrice(unitPrice);

        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.sku = sku.toUpperCase().trim();
        this.unitPrice = unitPrice;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adjusts the stock quantity by a delta value (positive or negative).
     *
     * @param delta the amount to add (positive) or subtract (negative)
     * @throws IllegalArgumentException if resulting stock would be negative
     */
    public void adjustStock(int delta) {
        int newQuantity = this.stockQuantity + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "Cannot reduce stock below zero. Current: " + stockQuantity + ", Delta: " + delta);
        }
        this.stockQuantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivates the product (soft delete).
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reactivates a previously deactivated product.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the product has sufficient stock for a given quantity.
     */
    public boolean hasSufficientStock(int requiredQuantity) {
        return this.stockQuantity >= requiredQuantity;
    }

    // =========================================================================
    // VALIDATION METHODS (Domain Invariants)
    // =========================================================================

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (name.trim().length() > 255) {
            throw new IllegalArgumentException("Product name cannot exceed 255 characters");
        }
    }

    private static void validateSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be null or empty");
        }
        if (!sku.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("Product SKU can only contain letters, numbers, and hyphens");
        }
    }

    private static void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
    }

    private static void validateStockQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Stock quantity cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    // =========================================================================
    // GETTERS (No setters - use domain methods for state changes)
    // =========================================================================

    public ProductId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSku() {
        return sku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // =========================================================================
    // EQUALS / HASHCODE (Based on ID for entities)
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        // For entities, equality is based on identity (ID)
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", active=" + active +
                '}';
    }
}

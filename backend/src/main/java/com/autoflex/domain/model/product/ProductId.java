package com.autoflex.domain.model.product;

import java.util.Objects;

/**
 * ProductId - Value Object representing a unique product identifier.
 *
 * <p>Immutable value object that encapsulates the product ID concept.
 * Using a Value Object instead of primitive Long provides:
 * - Type safety (cannot accidentally pass a RawMaterialId as ProductId)
 * - Self-validation
 * - Domain semantics
 *
 * <p><b>NOTE:</b> This is a pure domain object with NO framework annotations.
 */
public record ProductId(Long value) {

    public ProductId {
        Objects.requireNonNull(value, "ProductId value cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive number");
        }
    }

    /**
     * Factory method for creating ProductId from Long.
     *
     * @param value the numeric ID value
     * @return a new ProductId instance
     * @throws IllegalArgumentException if value is null or non-positive
     */
    public static ProductId of(Long value) {
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return "ProductId(" + value + ")";
    }
}

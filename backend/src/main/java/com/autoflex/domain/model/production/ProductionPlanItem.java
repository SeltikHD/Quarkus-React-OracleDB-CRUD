package com.autoflex.domain.model.production;

import com.autoflex.domain.model.product.ProductId;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * ProductionPlanItem - Value Object representing one item in a production plan.
 * Contains the product to be produced, the quantity, and the resulting value.
 */
public record ProductionPlanItem(
		ProductId productId,
		String productName,
		String productSku,
		int quantity,
		BigDecimal unitPrice,
		BigDecimal totalValue) {

	public ProductionPlanItem {
		Objects.requireNonNull(productId, "Product ID cannot be null");
		Objects.requireNonNull(productName, "Product name cannot be null");
		Objects.requireNonNull(productSku, "Product SKU cannot be null");
		if (quantity <= 0) {
			throw new IllegalArgumentException("Production quantity must be positive");
		}
		Objects.requireNonNull(unitPrice, "Unit price cannot be null");
		Objects.requireNonNull(totalValue, "Total value cannot be null");
	}
}

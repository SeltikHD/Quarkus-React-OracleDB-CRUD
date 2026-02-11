package com.autoflex.domain.model.production;

import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ProductionPlan - Value Object representing the result of a production
 * calculation.
 *
 * <p>
 * Contains the list of products to produce, total production value,
 * and the remaining raw material stock after production.
 */
public record ProductionPlan(
		List<ProductionPlanItem> items,
		BigDecimal totalProductionValue,
		Map<RawMaterialId, BigDecimal> remainingStock) {

	public ProductionPlan {
		Objects.requireNonNull(items, "Production plan items cannot be null");
		Objects.requireNonNull(totalProductionValue, "Total production value cannot be null");
		Objects.requireNonNull(remainingStock, "Remaining stock cannot be null");
		items = Collections.unmodifiableList(items);
		remainingStock = Collections.unmodifiableMap(remainingStock);
	}

	/** Returns true if the plan has at least one item to produce. */
	public boolean hasProduction() {
		return !items.isEmpty();
	}

	/** Returns the total number of product units across all items. */
	public int totalUnits() {
		return items.stream().mapToInt(ProductionPlanItem::quantity).sum();
	}
}

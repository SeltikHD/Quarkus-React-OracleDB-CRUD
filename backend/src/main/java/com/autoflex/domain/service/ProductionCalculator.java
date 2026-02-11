package com.autoflex.domain.service;

import com.autoflex.domain.model.product.BillOfMaterialItem;
import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.production.ProductionPlan;
import com.autoflex.domain.model.production.ProductionPlanItem;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProductionCalculator - Domain service implementing the Greedy Production
 * Algorithm.
 *
 * <h2>Algorithm: Greedy Resource Allocation</h2>
 *
 * <p>
 * The algorithm maximizes total production value by prioritizing products with
 * higher
 * unit prices. For each product (sorted by sales value descending), it
 * calculates the
 * maximum number of units that can be produced given available raw material
 * stock, then
 * allocates those materials before moving to the next product.
 *
 * <h3>Steps:</h3>
 * <ol>
 * <li>Build a mutable map of available raw material stock</li>
 * <li>Filter active products that have at least one BOM item</li>
 * <li>Sort products by unit price descending (greedy choice)</li>
 * <li>For each product:
 * <ul>
 * <li>For each BOM item, compute: available_stock / required_quantity</li>
 * <li>Max producible units = floor of the minimum across all BOM items</li>
 * <li>If max > 0, deduct consumed materials and record the production item</li>
 * </ul>
 * </li>
 * <li>Return the production plan with items, total value, and remaining
 * stock</li>
 * </ol>
 *
 * <h3>Greedy Justification:</h3>
 * <p>
 * By producing the most valuable products first, we ensure that scarce raw
 * materials
 * are consumed by products that generate the highest revenue. This is a locally
 * optimal
 * strategy that works well when product prices vary significantly.
 *
 * <h3>Time Complexity:</h3>
 * <p>
 * O(P * M) where P = number of products and M = max BOM items per product,
 * after an O(P log P) sort step.
 *
 * <p>
 * This is a pure domain service with NO framework dependencies.
 */
public final class ProductionCalculator {

	private ProductionCalculator() {
		// Utility class - prevent instantiation
	}

	/**
	 * Calculates the optimal production plan using the Greedy Algorithm.
	 *
	 * @param products     active products with their bill of materials
	 * @param rawMaterials available raw materials with current stock levels
	 * @return a ProductionPlan describing what to produce and remaining stock
	 * @throws IllegalArgumentException if products or rawMaterials are null
	 */
	public static ProductionPlan calculate(
			List<Product> products, List<RawMaterial> rawMaterials) {
		if (products == null) {
			throw new IllegalArgumentException("Products list cannot be null");
		}
		if (rawMaterials == null) {
			throw new IllegalArgumentException("Raw materials list cannot be null");
		}

		// Step 1: Build mutable stock map
		Map<RawMaterialId, BigDecimal> availableStock = buildStockMap(rawMaterials);

		// Step 2: Filter and sort products by unit price descending (greedy choice)
		List<Product> candidates = products.stream()
				.filter(Product::isActive)
				.filter(p -> !p.getMaterials().isEmpty())
				.sorted(Comparator.comparing(Product::getUnitPrice).reversed())
				.toList();

		// Step 3: Greedy allocation
		List<ProductionPlanItem> planItems = new ArrayList<>();

		for (Product product : candidates) {
			int maxUnits = calculateMaxProducibleUnits(product, availableStock);

			if (maxUnits <= 0) {
				continue;
			}

			// Allocate materials
			allocateMaterials(product, maxUnits, availableStock);

			// Record production item
			BigDecimal totalValue = product.getUnitPrice().multiply(BigDecimal.valueOf(maxUnits));

			planItems.add(
					new ProductionPlanItem(
							product.getId(),
							product.getName(),
							product.getSku(),
							maxUnits,
							product.getUnitPrice(),
							totalValue));
		}

		// Step 4: Calculate total production value
		BigDecimal totalProductionValue = planItems.stream()
				.map(ProductionPlanItem::totalValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new ProductionPlan(planItems, totalProductionValue, availableStock);
	}

	/**
	 * Calculates the maximum number of units of a product that can be produced
	 * given the available raw material stock.
	 *
	 * @param product        the product to evaluate
	 * @param availableStock current available stock for each raw material
	 * @return the maximum number of producible units (0 if any material is
	 *         insufficient)
	 */
	static int calculateMaxProducibleUnits(
			Product product, Map<RawMaterialId, BigDecimal> availableStock) {
		int maxUnits = Integer.MAX_VALUE;

		for (BillOfMaterialItem bom : product.getMaterials()) {
			BigDecimal available = availableStock.getOrDefault(bom.rawMaterialId(), BigDecimal.ZERO);

			if (available.compareTo(BigDecimal.ZERO) <= 0) {
				return 0;
			}

			int possibleUnits = available
					.divide(bom.quantityRequired(), 0, RoundingMode.FLOOR)
					.intValue();

			if (possibleUnits <= 0) {
				return 0;
			}

			maxUnits = Math.min(maxUnits, possibleUnits);
		}

		return maxUnits == Integer.MAX_VALUE ? 0 : maxUnits;
	}

	private static void allocateMaterials(
			Product product,
			int units,
			Map<RawMaterialId, BigDecimal> availableStock) {
		for (BillOfMaterialItem bom : product.getMaterials()) {
			BigDecimal consumed = bom.quantityRequired().multiply(BigDecimal.valueOf(units));
			availableStock.merge(bom.rawMaterialId(), consumed, BigDecimal::subtract);
		}
	}

	private static Map<RawMaterialId, BigDecimal> buildStockMap(
			List<RawMaterial> rawMaterials) {
		Map<RawMaterialId, BigDecimal> stockMap = new HashMap<>();
		for (RawMaterial rm : rawMaterials) {
			if (rm.isActive() && rm.getId() != null) {
				stockMap.put(rm.getId(), rm.getStockQuantity());
			}
		}
		return stockMap;
	}
}

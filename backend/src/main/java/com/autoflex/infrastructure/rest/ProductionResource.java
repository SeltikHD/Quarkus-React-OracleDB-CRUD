package com.autoflex.infrastructure.rest;

import com.autoflex.domain.model.production.ProductionPlan;
import com.autoflex.domain.model.production.ProductionPlanItem;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.in.ProductionUseCase;
import com.autoflex.infrastructure.rest.dto.ProductionPlanResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST resource for production planning operations.
 */
@Path("/api/v1/production")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Production", description = "Production planning and calculation operations")
public class ProductionResource {

	private final ProductionUseCase productionUseCase;

	@Inject
	public ProductionResource(ProductionUseCase productionUseCase) {
		this.productionUseCase = productionUseCase;
	}

	@POST
	@Path("/calculate")
	@Operation(summary = "Calculate optimal production plan", description = "Uses the Greedy Algorithm to calculate which products can be produced "
			+ "based on available raw material stock, prioritizing products with "
			+ "higher sales value (unit price).")
	public ProductionPlanResponse calculateProductionPlan() {
		ProductionPlan plan = productionUseCase.calculateProductionPlan();
		return toResponse(plan);
	}

	private ProductionPlanResponse toResponse(ProductionPlan plan) {
		var items = plan.items().stream()
				.map(this::toResponseItem)
				.collect(Collectors.toList());

		Map<Long, BigDecimal> remainingStock = plan.remainingStock().entrySet().stream()
				.collect(
						Collectors.toMap(
								e -> e.getKey().value(),
								Map.Entry::getValue));

		return new ProductionPlanResponse(
				items, plan.totalProductionValue(), plan.totalUnits(), remainingStock);
	}

	private ProductionPlanResponse.ProductionItem toResponseItem(ProductionPlanItem item) {
		return new ProductionPlanResponse.ProductionItem(
				item.productId().value(),
				item.productName(),
				item.productSku(),
				item.quantity(),
				item.unitPrice(),
				item.totalValue());
	}
}

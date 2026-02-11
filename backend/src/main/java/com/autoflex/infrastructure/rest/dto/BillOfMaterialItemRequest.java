package com.autoflex.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Request body for adding a raw material to a product's bill of materials.
 */
@Schema(name = "BillOfMaterialItemRequest", description = "Request body for adding a material to a product's BOM")
public class BillOfMaterialItemRequest {

	@NotNull(message = "Raw material ID is required")
	@Schema(description = "Raw material ID", example = "1", required = true)
	private Long rawMaterialId;

	@NotNull(message = "Quantity required is required")
	@DecimalMin(value = "0.0001", inclusive = true, message = "Quantity required must be positive")
	@Digits(integer = 15, fraction = 4, message = "Quantity format is invalid")
	@Schema(description = "Quantity of raw material needed to produce one unit of the product", example = "2.5", required = true)
	private BigDecimal quantityRequired;

	// Getters and Setters
	public Long getRawMaterialId() {
		return rawMaterialId;
	}

	public void setRawMaterialId(Long rawMaterialId) {
		this.rawMaterialId = rawMaterialId;
	}

	public BigDecimal getQuantityRequired() {
		return quantityRequired;
	}

	public void setQuantityRequired(BigDecimal quantityRequired) {
		this.quantityRequired = quantityRequired;
	}
}

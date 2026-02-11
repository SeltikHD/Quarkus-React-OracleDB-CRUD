package com.autoflex.infrastructure.rest.dto;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response body for a bill of material item.
 */
@Schema(name = "BillOfMaterialItemResponse", description = "Bill of material item information")
public class BillOfMaterialItemResponse {

	@Schema(description = "Raw material ID", example = "1")
	private Long rawMaterialId;

	@Schema(description = "Quantity of raw material needed per product unit", example = "2.5")
	private BigDecimal quantityRequired;

	public BillOfMaterialItemResponse() {
	}

	public BillOfMaterialItemResponse(Long rawMaterialId, BigDecimal quantityRequired) {
		this.rawMaterialId = rawMaterialId;
		this.quantityRequired = quantityRequired;
	}

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

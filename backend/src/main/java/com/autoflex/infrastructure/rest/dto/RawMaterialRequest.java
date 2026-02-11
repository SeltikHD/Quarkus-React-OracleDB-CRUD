package com.autoflex.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Request body for creating or updating a raw material.
 */
@Schema(name = "RawMaterialRequest", description = "Request body for creating or updating a raw material")
public class RawMaterialRequest {

	@NotBlank(message = "Raw material name is required")
	@Size(max = 255, message = "Name cannot exceed 255 characters")
	@Schema(description = "Raw material name", example = "Steel Sheet", required = true)
	private String name;

	@Size(max = 2000, message = "Description cannot exceed 2000 characters")
	@Schema(description = "Raw material description")
	private String description;

	@NotBlank(message = "Code is required")
	@Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Code can only contain letters, numbers, and hyphens")
	@Size(max = 50, message = "Code cannot exceed 50 characters")
	@Schema(description = "Unique material code", example = "RM-STEEL-001", required = true)
	private String code;

	@NotBlank(message = "Measurement unit is required")
	@Schema(description = "Measurement unit (KILOGRAM, GRAM, LITER, MILLILITER, METER, "
			+ "CENTIMETER, UNIT, PIECE, PAIR, BOX, ROLL, SHEET)", example = "KILOGRAM", required = true)
	private String unit;

	@NotNull(message = "Unit cost is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Unit cost cannot be negative")
	@Digits(integer = 15, fraction = 4, message = "Unit cost format is invalid")
	@Schema(description = "Cost per unit", example = "15.50", required = true)
	private BigDecimal unitCost;

	@DecimalMin(value = "0.0", inclusive = true, message = "Stock quantity cannot be negative")
	@Digits(integer = 15, fraction = 4, message = "Stock quantity format is invalid")
	@Schema(description = "Initial stock quantity", example = "500.00")
	private BigDecimal stockQuantity;

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public BigDecimal getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(BigDecimal stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
}

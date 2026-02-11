package com.autoflex.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Request body for updating the quantity of a material in a product's BOM.
 */
@Schema(
    name = "UpdateMaterialQuantityRequest",
    description = "Request body for updating material quantity in BOM")
public class UpdateMaterialQuantityRequest {

  @NotNull(message = "Quantity required is required")
  @DecimalMin(
      value = "0.0001",
      inclusive = true,
      message = "Quantity required must be positive")
  @Digits(
      integer = 15,
      fraction = 4,
      message = "Quantity format is invalid")
  @Schema(
      description = "New quantity of raw material needed per product unit",
      example = "25.0",
      required = true)
  private BigDecimal quantityRequired;

  public BigDecimal getQuantityRequired() {
    return quantityRequired;
  }

  public void setQuantityRequired(BigDecimal quantityRequired) {
    this.quantityRequired = quantityRequired;
  }
}

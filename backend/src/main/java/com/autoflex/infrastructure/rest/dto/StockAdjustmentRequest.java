package com.autoflex.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Request body for adjusting raw material stock quantity. */
@Schema(name = "StockAdjustmentRequest", description = "Request body for adjusting stock quantity")
public class StockAdjustmentRequest {

  @NotNull(message = "Quantity is required")
  @Schema(
      description = "Quantity to add (positive) or remove (negative) from current stock",
      example = "100.00",
      required = true)
  private BigDecimal quantity;

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }
}

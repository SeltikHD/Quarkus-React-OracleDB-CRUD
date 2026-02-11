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
 * ProductRequest - DTO for product creation and update requests.
 *
 * <p>This DTO handles API validation concerns separately from domain validation. The domain entity
 * performs its own validation to maintain invariants.
 */
@Schema(name = "ProductRequest", description = "Request body for creating or updating a product")
public class ProductRequest {

  @NotBlank(message = "Product name is required")
  @Size(max = 255, message = "Product name cannot exceed 255 characters")
  @Schema(description = "Product name", example = "Electronic Component XYZ", required = true)
  private String name;

  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  @Schema(
      description = "Product description",
      example = "High-quality electronic component for industrial use")
  private String description;

  @NotBlank(message = "SKU is required")
  @Pattern(
      regexp = "^[A-Za-z0-9-]+$",
      message = "SKU can only contain letters, numbers, and hyphens")
  @Size(max = 50, message = "SKU cannot exceed 50 characters")
  @Schema(
      description = "Stock Keeping Unit (unique identifier)",
      example = "COMP-XYZ-001",
      required = true)
  private String sku;

  @NotNull(message = "Unit price is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
  @Digits(integer = 15, fraction = 4, message = "Unit price format is invalid")
  @Schema(description = "Unit price in the default currency", example = "29.99", required = true)
  private BigDecimal unitPrice;

  @Min(value = 0, message = "Stock quantity cannot be negative")
  @Schema(description = "Initial stock quantity", example = "100")
  private Integer stockQuantity = 0;

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

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Integer getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(Integer stockQuantity) {
    this.stockQuantity = stockQuantity;
  }
}

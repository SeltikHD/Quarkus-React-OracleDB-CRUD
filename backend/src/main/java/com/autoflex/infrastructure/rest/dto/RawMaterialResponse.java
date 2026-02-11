package com.autoflex.infrastructure.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Response body for raw material information. */
@Schema(name = "RawMaterialResponse", description = "Raw material information returned by the API")
public class RawMaterialResponse {

  @Schema(description = "Unique raw material identifier", example = "1")
  private Long id;

  @Schema(description = "Raw material name", example = "Steel Sheet")
  private String name;

  @Schema(description = "Raw material description")
  private String description;

  @Schema(description = "Unique material code", example = "RM-STEEL-001")
  private String code;

  @Schema(description = "Measurement unit", example = "KILOGRAM")
  private String unit;

  @Schema(description = "Unit abbreviation", example = "kg")
  private String unitAbbreviation;

  @Schema(description = "Current stock quantity", example = "500.00")
  private BigDecimal stockQuantity;

  @Schema(description = "Cost per unit", example = "15.50")
  private BigDecimal unitCost;

  @Schema(description = "Whether the raw material is active", example = "true")
  private boolean active;

  @Schema(description = "Creation timestamp")
  private LocalDateTime createdAt;

  @Schema(description = "Last update timestamp")
  private LocalDateTime updatedAt;

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final RawMaterialResponse response = new RawMaterialResponse();

    public Builder id(Long id) {
      response.id = id;
      return this;
    }

    public Builder name(String name) {
      response.name = name;
      return this;
    }

    public Builder description(String description) {
      response.description = description;
      return this;
    }

    public Builder code(String code) {
      response.code = code;
      return this;
    }

    public Builder unit(String unit) {
      response.unit = unit;
      return this;
    }

    public Builder unitAbbreviation(String unitAbbreviation) {
      response.unitAbbreviation = unitAbbreviation;
      return this;
    }

    public Builder stockQuantity(BigDecimal stockQuantity) {
      response.stockQuantity = stockQuantity;
      return this;
    }

    public Builder unitCost(BigDecimal unitCost) {
      response.unitCost = unitCost;
      return this;
    }

    public Builder active(boolean active) {
      response.active = active;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      response.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(LocalDateTime updatedAt) {
      response.updatedAt = updatedAt;
      return this;
    }

    public RawMaterialResponse build() {
      return response;
    }
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getCode() {
    return code;
  }

  public String getUnit() {
    return unit;
  }

  public String getUnitAbbreviation() {
    return unitAbbreviation;
  }

  public BigDecimal getStockQuantity() {
    return stockQuantity;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public boolean isActive() {
    return active;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}

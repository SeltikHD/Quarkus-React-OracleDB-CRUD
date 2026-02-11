package com.autoflex.infrastructure.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * ProductResponse - DTO for product API responses.
 *
 * <p>This DTO is tailored for API consumers and may differ from the domain model. It includes only
 * the data needed by the frontend/clients.
 */
@Schema(name = "ProductResponse", description = "Product information returned by the API")
public class ProductResponse {

  @Schema(description = "Unique product identifier", example = "1")
  private Long id;

  @Schema(description = "Product name", example = "Electronic Component XYZ")
  private String name;

  @Schema(description = "Product description", example = "High-quality electronic component")
  private String description;

  @Schema(description = "Stock Keeping Unit", example = "COMP-XYZ-001")
  private String sku;

  @Schema(description = "Unit price", example = "29.99")
  private BigDecimal unitPrice;

  @Schema(description = "Current stock quantity", example = "100")
  private Integer stockQuantity;

  @Schema(description = "Whether the product is active", example = "true")
  private boolean active;

  @Schema(description = "Creation timestamp")
  private LocalDateTime createdAt;

  @Schema(description = "Last update timestamp")
  private LocalDateTime updatedAt;

  @Schema(description = "Bill of materials (raw material requirements)")
  private List<BillOfMaterialItemResponse> materials;

  // Builder pattern for clean construction

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final ProductResponse response = new ProductResponse();

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

    public Builder sku(String sku) {
      response.sku = sku;
      return this;
    }

    public Builder unitPrice(BigDecimal unitPrice) {
      response.unitPrice = unitPrice;
      return this;
    }

    public Builder stockQuantity(Integer stockQuantity) {
      response.stockQuantity = stockQuantity;
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

    public Builder materials(List<BillOfMaterialItemResponse> materials) {
      response.materials = materials;
      return this;
    }

    public ProductResponse build() {
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

  public String getSku() {
    return sku;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public Integer getStockQuantity() {
    return stockQuantity;
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

  public List<BillOfMaterialItemResponse> getMaterials() {
    return materials;
  }
}

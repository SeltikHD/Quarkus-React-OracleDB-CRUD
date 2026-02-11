package com.autoflex.infrastructure.rest.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Response body for a production plan calculation result. */
@Schema(
    name = "ProductionPlanResponse",
    description = "Result of the greedy production calculation algorithm")
public class ProductionPlanResponse {

  @Schema(description = "List of products to produce with quantities")
  private List<ProductionItem> items;

  @Schema(description = "Total production value across all items", example = "15750.00")
  private BigDecimal totalProductionValue;

  @Schema(description = "Total number of product units to produce", example = "250")
  private int totalUnits;

  @Schema(description = "Remaining raw material stock after production")
  private Map<Long, BigDecimal> remainingStock;

  public static class ProductionItem {

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "Electronic Component XYZ")
    private String productName;

    @Schema(description = "Product SKU", example = "COMP-XYZ-001")
    private String productSku;

    @Schema(description = "Number of units to produce", example = "50")
    private int quantity;

    @Schema(description = "Unit price of the product", example = "29.99")
    private BigDecimal unitPrice;

    @Schema(description = "Total value for this product line", example = "1499.50")
    private BigDecimal totalValue;

    public ProductionItem() {}

    public ProductionItem(
        Long productId,
        String productName,
        String productSku,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalValue) {
      this.productId = productId;
      this.productName = productName;
      this.productSku = productSku;
      this.quantity = quantity;
      this.unitPrice = unitPrice;
      this.totalValue = totalValue;
    }

    // Getters
    public Long getProductId() {
      return productId;
    }

    public String getProductName() {
      return productName;
    }

    public String getProductSku() {
      return productSku;
    }

    public int getQuantity() {
      return quantity;
    }

    public BigDecimal getUnitPrice() {
      return unitPrice;
    }

    public BigDecimal getTotalValue() {
      return totalValue;
    }
  }

  public ProductionPlanResponse() {}

  public ProductionPlanResponse(
      List<ProductionItem> items,
      BigDecimal totalProductionValue,
      int totalUnits,
      Map<Long, BigDecimal> remainingStock) {
    this.items = items;
    this.totalProductionValue = totalProductionValue;
    this.totalUnits = totalUnits;
    this.remainingStock = remainingStock;
  }

  // Getters
  public List<ProductionItem> getItems() {
    return items;
  }

  public BigDecimal getTotalProductionValue() {
    return totalProductionValue;
  }

  public int getTotalUnits() {
    return totalUnits;
  }

  public Map<Long, BigDecimal> getRemainingStock() {
    return remainingStock;
  }
}

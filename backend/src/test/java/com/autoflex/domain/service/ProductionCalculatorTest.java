package com.autoflex.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.autoflex.domain.model.product.BillOfMaterialItem;
import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;
import com.autoflex.domain.model.production.ProductionPlan;
import com.autoflex.domain.model.production.ProductionPlanItem;
import com.autoflex.domain.model.rawmaterial.MeasurementUnit;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ProductionCalculator - Greedy Algorithm")
class ProductionCalculatorTest {

  // Helper methods for creating test fixtures
  private static RawMaterial createRawMaterial(long id, String name, String stock) {
    return RawMaterial.reconstitute(
        RawMaterialId.of(id),
        name,
        null,
        "RM-" + id,
        MeasurementUnit.UNIT,
        new BigDecimal(stock),
        BigDecimal.TEN,
        true,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  private static Product createProduct(
      long id, String name, String price, List<BillOfMaterialItem> bom) {
    return Product.reconstitute(
        ProductId.of(id),
        name,
        null,
        "SKU-" + id,
        new BigDecimal(price),
        0,
        true,
        LocalDateTime.now(),
        LocalDateTime.now(),
        bom);
  }

  @Nested
  @DisplayName("Basic Scenarios")
  class BasicScenarios {

    @Test
    @DisplayName("should return empty plan when no products are provided")
    void shouldReturnEmptyPlanForNoProducts() {
      List<RawMaterial> materials = List.of(createRawMaterial(1, "Steel", "100"));
      ProductionPlan plan = ProductionCalculator.calculate(List.of(), materials);

      assertThat(plan.hasProduction()).isFalse();
      assertThat(plan.items()).isEmpty();
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("should return empty plan when no raw materials are available")
    void shouldReturnEmptyPlanForNoMaterials() {
      Product product =
          createProduct(
              1,
              "Widget",
              "100",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("5"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(product), List.of());

      assertThat(plan.hasProduction()).isFalse();
      assertThat(plan.items()).isEmpty();
    }

    @Test
    @DisplayName("should produce single product with single material")
    void shouldProduceSingleProductWithSingleMaterial() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      Product widget =
          createProduct(
              1,
              "Widget",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(steel));

      assertThat(plan.hasProduction()).isTrue();
      assertThat(plan.items()).hasSize(1);

      ProductionPlanItem item = plan.items().get(0);
      assertThat(item.productId()).isEqualTo(ProductId.of(1L));
      assertThat(item.quantity()).isEqualTo(10); // 100 / 10 = 10 units
      assertThat(item.totalValue()).isEqualByComparingTo(new BigDecimal("500")); // 10 * 50
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    @DisplayName("should produce product with multiple materials")
    void shouldProduceWithMultipleMaterials() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      RawMaterial rubber = createRawMaterial(2, "Rubber", "30");

      // Widget needs 10 steel + 5 rubber per unit
      Product widget =
          createProduct(
              1,
              "Widget",
              "100",
              List.of(
                  BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10")),
                  BillOfMaterialItem.of(RawMaterialId.of(2L), new BigDecimal("5"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(steel, rubber));

      assertThat(plan.items()).hasSize(1);
      // steel allows 10 units (100/10), rubber allows 6 units (30/5)
      // min = 6 units
      assertThat(plan.items().get(0).quantity()).isEqualTo(6);
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(new BigDecimal("600"));
    }
  }

  @Nested
  @DisplayName("Greedy Prioritization")
  class GreedyPrioritization {

    @Test
    @DisplayName("should prioritize products with higher unit price")
    void shouldPrioritizeHigherPrice() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");

      // Cheap product: needs 10 steel per unit, price $10
      Product cheapProduct =
          createProduct(
              1,
              "Cheap Widget",
              "10",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      // Expensive product: needs 10 steel per unit, price $100
      Product expensiveProduct =
          createProduct(
              2,
              "Premium Widget",
              "100",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      ProductionPlan plan =
          ProductionCalculator.calculate(List.of(cheapProduct, expensiveProduct), List.of(steel));

      // Expensive product should be produced first (greedy)
      assertThat(plan.items()).hasSize(1);
      assertThat(plan.items().get(0).productId()).isEqualTo(ProductId.of(2L));
      assertThat(plan.items().get(0).productName()).isEqualTo("Premium Widget");
      // 100 / 10 = 10 units of expensive product, consuming all steel
      assertThat(plan.items().get(0).quantity()).isEqualTo(10);
      // Total = 10 * 100 = 1000
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("should produce multiple products when materials allow")
    void shouldProduceMultipleProducts() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      RawMaterial plastic = createRawMaterial(2, "Plastic", "50");

      // Product A: $50, needs 10 steel (no plastic)
      Product productA =
          createProduct(
              1,
              "Product A",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      // Product B: $80, needs 5 plastic (no steel)
      Product productB =
          createProduct(
              2,
              "Product B",
              "80",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(2L), new BigDecimal("5"))));

      ProductionPlan plan =
          ProductionCalculator.calculate(List.of(productA, productB), List.of(steel, plastic));

      // Both products can be produced (non-competing materials)
      assertThat(plan.items()).hasSize(2);
      // B first (higher price), then A
      assertThat(plan.items().get(0).productId()).isEqualTo(ProductId.of(2L));
      assertThat(plan.items().get(0).quantity()).isEqualTo(10); // 50 / 5
      assertThat(plan.items().get(1).productId()).isEqualTo(ProductId.of(1L));
      assertThat(plan.items().get(1).quantity()).isEqualTo(10); // 100 / 10
    }

    @Test
    @DisplayName("should allocate remaining materials to cheaper products")
    void shouldAllocateRemainingToCheaper() {
      RawMaterial steel = createRawMaterial(1, "Steel", "25");

      // Expensive: $100, needs 10 steel
      Product expensive =
          createProduct(
              1,
              "Expensive",
              "100",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      // Cheap: $30, needs 5 steel
      Product cheap =
          createProduct(
              2,
              "Cheap",
              "30",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("5"))));

      ProductionPlan plan =
          ProductionCalculator.calculate(List.of(cheap, expensive), List.of(steel));

      assertThat(plan.items()).hasSize(2);
      // Expensive first: 25/10 = 2 units (uses 20 steel, 5 remaining)
      assertThat(plan.items().get(0).productId()).isEqualTo(ProductId.of(1L));
      assertThat(plan.items().get(0).quantity()).isEqualTo(2);
      // Cheap second: 5/5 = 1 unit (uses remaining 5 steel)
      assertThat(plan.items().get(1).productId()).isEqualTo(ProductId.of(2L));
      assertThat(plan.items().get(1).quantity()).isEqualTo(1);

      BigDecimal expectedTotal = new BigDecimal("230"); // 200 + 30
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(expectedTotal);
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCases {

    @Test
    @DisplayName("should skip products without BOM")
    void shouldSkipProductsWithoutBom() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      Product noBom = createProduct(1, "No BOM Product", "100", List.of());

      ProductionPlan plan = ProductionCalculator.calculate(List.of(noBom), List.of(steel));

      assertThat(plan.hasProduction()).isFalse();
    }

    @Test
    @DisplayName("should skip inactive products")
    void shouldSkipInactiveProducts() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      Product inactive =
          Product.reconstitute(
              ProductId.of(1L),
              "Inactive",
              null,
              "SKU-1",
              new BigDecimal("100"),
              0,
              false, // inactive
              LocalDateTime.now(),
              LocalDateTime.now(),
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(inactive), List.of(steel));

      assertThat(plan.hasProduction()).isFalse();
    }

    @Test
    @DisplayName("should handle zero stock of required material")
    void shouldHandleZeroStock() {
      RawMaterial emptySteel = createRawMaterial(1, "Steel", "0");
      Product widget =
          createProduct(
              1,
              "Widget",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(emptySteel));

      assertThat(plan.hasProduction()).isFalse();
    }

    @Test
    @DisplayName("should handle insufficient stock for even one unit")
    void shouldHandleInsufficientStock() {
      RawMaterial steel = createRawMaterial(1, "Steel", "9");
      Product widget =
          createProduct(
              1,
              "Widget",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(steel));

      assertThat(plan.hasProduction()).isFalse();
    }

    @Test
    @DisplayName("should handle product requiring a material not in stock")
    void shouldHandleMissingMaterial() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      Product widget =
          createProduct(
              1,
              "Widget",
              "50",
              List.of(
                  BillOfMaterialItem.of(
                      RawMaterialId.of(99L), new BigDecimal("10")))); // Material 99 not in stock

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(steel));

      assertThat(plan.hasProduction()).isFalse();
    }

    @Test
    @DisplayName("should track remaining stock correctly")
    void shouldTrackRemainingStock() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      Product widget =
          createProduct(
              1,
              "Widget",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("30"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(steel));

      // 100 / 30 = 3 units (uses 90 steel, 10 remaining)
      assertThat(plan.items().get(0).quantity()).isEqualTo(3);
      assertThat(plan.remainingStock().get(RawMaterialId.of(1L)))
          .isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    @DisplayName("should reject null products list")
    void shouldRejectNullProducts() {
      assertThatThrownBy(() -> ProductionCalculator.calculate(null, List.of()))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Products");
    }

    @Test
    @DisplayName("should reject null raw materials list")
    void shouldRejectNullMaterials() {
      assertThatThrownBy(() -> ProductionCalculator.calculate(List.of(), null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Raw materials");
    }

    @Test
    @DisplayName("should return correct total units")
    void shouldReturnCorrectTotalUnits() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      RawMaterial plastic = createRawMaterial(2, "Plastic", "50");

      Product a =
          createProduct(
              1,
              "A",
              "80",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));
      Product b =
          createProduct(
              2,
              "B",
              "60",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(2L), new BigDecimal("5"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(a, b), List.of(steel, plastic));

      // A: 10 units, B: 10 units
      assertThat(plan.totalUnits()).isEqualTo(20);
    }

    @Test
    @DisplayName("should handle fractional material requirements")
    void shouldHandleFractionalRequirements() {
      RawMaterial paint = createRawMaterial(1, "Paint", "10.5");
      Product door =
          createProduct(
              1,
              "Door",
              "200",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("2.5"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(door), List.of(paint));

      // 10.5 / 2.5 = 4.2 -> floor to 4 units
      assertThat(plan.items().get(0).quantity()).isEqualTo(4);
      // Remaining: 10.5 - (4 * 2.5) = 10.5 - 10.0 = 0.5
      assertThat(plan.remainingStock().get(RawMaterialId.of(1L)))
          .isEqualByComparingTo(new BigDecimal("0.5"));
    }

    @Test
    @DisplayName("should skip inactive raw materials in stock map")
    void shouldSkipInactiveRawMaterials() {
      RawMaterial inactive =
          RawMaterial.reconstitute(
              RawMaterialId.of(1L),
              "Inactive Steel",
              null,
              "RM-1",
              MeasurementUnit.UNIT,
              new BigDecimal("1000"),
              BigDecimal.TEN,
              false, // inactive
              LocalDateTime.now(),
              LocalDateTime.now());

      Product widget =
          createProduct(
              1,
              "Widget",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));

      ProductionPlan plan = ProductionCalculator.calculate(List.of(widget), List.of(inactive));

      assertThat(plan.hasProduction()).isFalse();
    }
  }

  @Nested
  @DisplayName("Complex Scenarios")
  class ComplexScenarios {

    @Test
    @DisplayName("should handle three products competing for same material")
    void shouldHandleThreeProductsCompeting() {
      RawMaterial steel = createRawMaterial(1, "Steel", "50");

      Product premium =
          createProduct(
              1,
              "Premium",
              "200",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("20"))));
      Product standard =
          createProduct(
              2,
              "Standard",
              "100",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10"))));
      Product budget =
          createProduct(
              3,
              "Budget",
              "50",
              List.of(BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("5"))));

      ProductionPlan plan =
          ProductionCalculator.calculate(List.of(budget, standard, premium), List.of(steel));

      // Premium first: 50/20 = 2 units (uses 40, 10 remaining)
      // Standard next: 10/10 = 1 unit (uses 10, 0 remaining)
      // Budget: 0/5 = 0 (no steel left)
      assertThat(plan.items()).hasSize(2);
      assertThat(plan.items().get(0).productId()).isEqualTo(ProductId.of(1L));
      assertThat(plan.items().get(0).quantity()).isEqualTo(2);
      assertThat(plan.items().get(1).productId()).isEqualTo(ProductId.of(2L));
      assertThat(plan.items().get(1).quantity()).isEqualTo(1);

      BigDecimal expectedTotal = new BigDecimal("500"); // 400 + 100
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(expectedTotal);
    }

    @Test
    @DisplayName("should handle products with shared and unique materials")
    void shouldHandleSharedAndUniqueMaterials() {
      RawMaterial steel = createRawMaterial(1, "Steel", "100");
      RawMaterial gold = createRawMaterial(2, "Gold", "10");
      RawMaterial plastic = createRawMaterial(3, "Plastic", "200");

      // Luxury: $500, needs 20 steel + 5 gold
      Product luxury =
          createProduct(
              1,
              "Luxury",
              "500",
              List.of(
                  BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("20")),
                  BillOfMaterialItem.of(RawMaterialId.of(2L), new BigDecimal("5"))));

      // Standard: $100, needs 10 steel + 10 plastic
      Product standard =
          createProduct(
              2,
              "Standard",
              "100",
              List.of(
                  BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("10")),
                  BillOfMaterialItem.of(RawMaterialId.of(3L), new BigDecimal("10"))));

      ProductionPlan plan =
          ProductionCalculator.calculate(List.of(standard, luxury), List.of(steel, gold, plastic));

      // Luxury first ($500): min(100/20=5, 10/5=2) = 2 units
      //   Uses: 40 steel, 10 gold. Remaining: 60 steel, 0 gold, 200 plastic
      // Standard ($100): min(60/10=6, 200/10=20) = 6 units
      //   Uses: 60 steel, 60 plastic. Remaining: 0 steel, 0 gold, 140 plastic
      assertThat(plan.items()).hasSize(2);
      assertThat(plan.items().get(0).productId()).isEqualTo(ProductId.of(1L));
      assertThat(plan.items().get(0).quantity()).isEqualTo(2);
      assertThat(plan.items().get(1).productId()).isEqualTo(ProductId.of(2L));
      assertThat(plan.items().get(1).quantity()).isEqualTo(6);

      BigDecimal expectedTotal = new BigDecimal("1600"); // 1000 + 600
      assertThat(plan.totalProductionValue()).isEqualByComparingTo(expectedTotal);
    }
  }
}

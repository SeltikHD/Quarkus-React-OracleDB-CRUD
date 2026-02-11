package com.autoflex.domain.model.rawmaterial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RawMaterial Domain Entity")
class RawMaterialTest {

  @Nested
  @DisplayName("RawMaterial Creation")
  class RawMaterialCreation {

    @Test
    @DisplayName("should create a valid raw material with all required fields")
    void shouldCreateValidRawMaterial() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel Sheet",
              "High-grade steel",
              "RM-STEEL-001",
              MeasurementUnit.KILOGRAM,
              new BigDecimal("500.00"),
              new BigDecimal("15.50"));

      assertThat(rm.getName()).isEqualTo("Steel Sheet");
      assertThat(rm.getDescription()).isEqualTo("High-grade steel");
      assertThat(rm.getCode()).isEqualTo("RM-STEEL-001");
      assertThat(rm.getUnit()).isEqualTo(MeasurementUnit.KILOGRAM);
      assertThat(rm.getStockQuantity()).isEqualByComparingTo(new BigDecimal("500.00"));
      assertThat(rm.getUnitCost()).isEqualByComparingTo(new BigDecimal("15.50"));
      assertThat(rm.isActive()).isTrue();
      assertThat(rm.getId()).isNull();
      assertThat(rm.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should uppercase code during creation")
    void shouldUppercaseCode() {
      RawMaterial rm =
          RawMaterial.create(
              "Material",
              null,
              "rm-abc-123",
              MeasurementUnit.UNIT,
              BigDecimal.ZERO,
              BigDecimal.TEN);
      assertThat(rm.getCode()).isEqualTo("RM-ABC-123");
    }

    @Test
    @DisplayName("should reject null name")
    void shouldRejectNullName() {
      assertThatThrownBy(
              () ->
                  RawMaterial.create(
                      null, null, "RM-001", MeasurementUnit.UNIT, BigDecimal.ZERO, BigDecimal.TEN))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("name");
    }

    @Test
    @DisplayName("should reject empty name")
    void shouldRejectEmptyName() {
      assertThatThrownBy(
              () ->
                  RawMaterial.create(
                      "   ", null, "RM-001", MeasurementUnit.UNIT, BigDecimal.ZERO, BigDecimal.TEN))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("name");
    }

    @Test
    @DisplayName("should reject invalid code characters")
    void shouldRejectInvalidCode() {
      assertThatThrownBy(
              () ->
                  RawMaterial.create(
                      "Material",
                      null,
                      "RM@001",
                      MeasurementUnit.UNIT,
                      BigDecimal.ZERO,
                      BigDecimal.TEN))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("code");
    }

    @Test
    @DisplayName("should reject null measurement unit")
    void shouldRejectNullUnit() {
      assertThatThrownBy(
              () ->
                  RawMaterial.create(
                      "Material", null, "RM-001", null, BigDecimal.ZERO, BigDecimal.TEN))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should reject negative stock quantity")
    void shouldRejectNegativeStock() {
      assertThatThrownBy(
              () ->
                  RawMaterial.create(
                      "Material",
                      null,
                      "RM-001",
                      MeasurementUnit.UNIT,
                      new BigDecimal("-1"),
                      BigDecimal.TEN))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("quantity");
    }

    @Test
    @DisplayName("should reject negative unit cost")
    void shouldRejectNegativeUnitCost() {
      assertThatThrownBy(
              () ->
                  RawMaterial.create(
                      "Material",
                      null,
                      "RM-001",
                      MeasurementUnit.UNIT,
                      BigDecimal.ZERO,
                      new BigDecimal("-5")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("cost");
    }
  }

  @Nested
  @DisplayName("Stock Management")
  class StockManagement {

    @Test
    @DisplayName("should increase stock correctly")
    void shouldIncreaseStock() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, new BigDecimal("100"), BigDecimal.TEN);
      rm.adjustStock(new BigDecimal("50.5"));
      assertThat(rm.getStockQuantity()).isEqualByComparingTo(new BigDecimal("150.5"));
    }

    @Test
    @DisplayName("should decrease stock correctly")
    void shouldDecreaseStock() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, new BigDecimal("100"), BigDecimal.TEN);
      rm.adjustStock(new BigDecimal("-30.25"));
      assertThat(rm.getStockQuantity()).isEqualByComparingTo(new BigDecimal("69.75"));
    }

    @Test
    @DisplayName("should reject stock reduction below zero")
    void shouldRejectStockBelowZero() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, new BigDecimal("50"), BigDecimal.TEN);
      assertThatThrownBy(() -> rm.adjustStock(new BigDecimal("-51")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("below zero");
    }

    @Test
    @DisplayName("should correctly check sufficient stock")
    void shouldCheckSufficientStock() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, new BigDecimal("100"), BigDecimal.TEN);
      assertThat(rm.hasSufficientStock(new BigDecimal("100"))).isTrue();
      assertThat(rm.hasSufficientStock(new BigDecimal("99.99"))).isTrue();
      assertThat(rm.hasSufficientStock(new BigDecimal("100.01"))).isFalse();
    }
  }

  @Nested
  @DisplayName("RawMaterial Lifecycle")
  class Lifecycle {

    @Test
    @DisplayName("should deactivate raw material")
    void shouldDeactivate() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, BigDecimal.ZERO, BigDecimal.TEN);
      assertThat(rm.isActive()).isTrue();
      rm.deactivate();
      assertThat(rm.isActive()).isFalse();
    }

    @Test
    @DisplayName("should reactivate raw material")
    void shouldReactivate() {
      RawMaterial rm =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, BigDecimal.ZERO, BigDecimal.TEN);
      rm.deactivate();
      rm.activate();
      assertThat(rm.isActive()).isTrue();
    }

    @Test
    @DisplayName("should update raw material information")
    void shouldUpdate() {
      RawMaterial rm =
          RawMaterial.create(
              "Original", "Desc", "RM-001", MeasurementUnit.KILOGRAM, BigDecimal.ZERO, BigDecimal.TEN);
      rm.update("Updated", "New Desc", "RM-002", MeasurementUnit.LITER, new BigDecimal("20"));

      assertThat(rm.getName()).isEqualTo("Updated");
      assertThat(rm.getDescription()).isEqualTo("New Desc");
      assertThat(rm.getCode()).isEqualTo("RM-002");
      assertThat(rm.getUnit()).isEqualTo(MeasurementUnit.LITER);
      assertThat(rm.getUnitCost()).isEqualByComparingTo(new BigDecimal("20"));
    }
  }

  @Nested
  @DisplayName("MeasurementUnit")
  class MeasurementUnitTests {

    @Test
    @DisplayName("should find unit by abbreviation")
    void shouldFindByAbbreviation() {
      assertThat(MeasurementUnit.fromAbbreviation("kg")).isEqualTo(MeasurementUnit.KILOGRAM);
      assertThat(MeasurementUnit.fromAbbreviation("L")).isEqualTo(MeasurementUnit.LITER);
      assertThat(MeasurementUnit.fromAbbreviation("un")).isEqualTo(MeasurementUnit.UNIT);
    }

    @Test
    @DisplayName("should reject unknown abbreviation")
    void shouldRejectUnknownAbbreviation() {
      assertThatThrownBy(() -> MeasurementUnit.fromAbbreviation("xyz"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unknown");
    }
  }
}

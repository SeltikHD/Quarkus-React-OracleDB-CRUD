package com.autoflex.domain.model.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Product Bill of Materials")
class ProductBomTest {

  @Nested
  @DisplayName("Adding Materials")
  class AddingMaterials {

    @Test
    @DisplayName("should add material to product BOM")
    void shouldAddMaterial() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5.0"));

      assertThat(product.getMaterials()).hasSize(1);
      assertThat(product.getMaterials().get(0).rawMaterialId()).isEqualTo(RawMaterialId.of(1L));
      assertThat(product.getMaterials().get(0).quantityRequired())
          .isEqualByComparingTo(new BigDecimal("5.0"));
    }

    @Test
    @DisplayName("should add multiple materials")
    void shouldAddMultipleMaterials() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));
      product.addMaterial(RawMaterialId.of(2L), new BigDecimal("10"));

      assertThat(product.getMaterials()).hasSize(2);
    }

    @Test
    @DisplayName("should reject duplicate material")
    void shouldRejectDuplicateMaterial() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));

      assertThatThrownBy(() -> product.addMaterial(RawMaterialId.of(1L), new BigDecimal("10")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("already in the bill of materials");
    }

    @Test
    @DisplayName("should reject null material ID")
    void shouldRejectNullMaterialId() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);

      assertThatThrownBy(() -> product.addMaterial(null, new BigDecimal("5")))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should reject zero quantity")
    void shouldRejectZeroQuantity() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);

      assertThatThrownBy(() -> product.addMaterial(RawMaterialId.of(1L), BigDecimal.ZERO))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("positive");
    }

    @Test
    @DisplayName("should reject negative quantity")
    void shouldRejectNegativeQuantity() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);

      assertThatThrownBy(
              () -> product.addMaterial(RawMaterialId.of(1L), new BigDecimal("-1")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("positive");
    }
  }

  @Nested
  @DisplayName("Removing Materials")
  class RemovingMaterials {

    @Test
    @DisplayName("should remove material from BOM")
    void shouldRemoveMaterial() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));
      product.addMaterial(RawMaterialId.of(2L), new BigDecimal("10"));

      product.removeMaterial(RawMaterialId.of(1L));

      assertThat(product.getMaterials()).hasSize(1);
      assertThat(product.getMaterials().get(0).rawMaterialId()).isEqualTo(RawMaterialId.of(2L));
    }

    @Test
    @DisplayName("should throw when removing non-existent material")
    void shouldThrowWhenRemovingNonExistent() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);

      assertThatThrownBy(() -> product.removeMaterial(RawMaterialId.of(99L)))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("not in the bill of materials");
    }
  }

  @Nested
  @DisplayName("Updating Material Quantity")
  class UpdatingMaterialQuantity {

    @Test
    @DisplayName("should update material quantity")
    void shouldUpdateQuantity() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));

      product.updateMaterialQuantity(RawMaterialId.of(1L), new BigDecimal("15"));

      assertThat(product.getMaterials().get(0).quantityRequired())
          .isEqualByComparingTo(new BigDecimal("15"));
    }

    @Test
    @DisplayName("should throw when updating non-existent material")
    void shouldThrowWhenUpdatingNonExistent() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);

      assertThatThrownBy(
              () -> product.updateMaterialQuantity(RawMaterialId.of(99L), new BigDecimal("5")))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("not in the bill of materials");
    }

    @Test
    @DisplayName("should reject zero quantity on update")
    void shouldRejectZeroQuantityOnUpdate() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));

      assertThatThrownBy(
              () -> product.updateMaterialQuantity(RawMaterialId.of(1L), BigDecimal.ZERO))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("positive");
    }
  }

  @Nested
  @DisplayName("Materials Immutability")
  class MaterialsImmutability {

    @Test
    @DisplayName("should return unmodifiable list")
    void shouldReturnUnmodifiableList() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      product.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));

      assertThatThrownBy(
              () ->
                  product
                      .getMaterials()
                      .add(BillOfMaterialItem.of(RawMaterialId.of(2L), BigDecimal.ONE)))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should start with empty materials on creation")
    void shouldStartEmptyOnCreation() {
      Product product = Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 0);
      assertThat(product.getMaterials()).isEmpty();
    }

    @Test
    @DisplayName("should reconstitute with materials")
    void shouldReconstituteWithMaterials() {
      var materials =
          java.util.List.of(
              BillOfMaterialItem.of(RawMaterialId.of(1L), new BigDecimal("5")),
              BillOfMaterialItem.of(RawMaterialId.of(2L), new BigDecimal("10")));

      Product product =
          Product.reconstitute(
              ProductId.of(1L),
              "Widget",
              null,
              "SKU-001",
              BigDecimal.TEN,
              0,
              true,
              java.time.LocalDateTime.now(),
              java.time.LocalDateTime.now(),
              materials);

      assertThat(product.getMaterials()).hasSize(2);
    }
  }
}

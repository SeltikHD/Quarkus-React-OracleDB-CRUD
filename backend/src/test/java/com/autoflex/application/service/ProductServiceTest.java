package com.autoflex.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.in.ProductUseCase;
import com.autoflex.domain.port.in.ProductUseCase.AddMaterialCommand;
import com.autoflex.domain.port.in.ProductUseCase.CreateProductCommand;
import com.autoflex.domain.port.in.ProductUseCase.ProductNotFoundException;
import com.autoflex.domain.port.in.ProductUseCase.ProductSkuAlreadyExistsException;
import com.autoflex.domain.port.out.ProductRepository;
import com.autoflex.domain.port.out.RawMaterialRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ProductService")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  @Mock private RawMaterialRepository rawMaterialRepository;

  @InjectMocks private ProductService productService;

  @Nested
  @DisplayName("When creating a product")
  class CreateProduct {

    @Test
    @DisplayName("should create product with valid data")
    void shouldCreateProduct() {
      var command = new CreateProductCommand("Widget", "A widget", "SKU-001", BigDecimal.TEN, 100);

      when(productRepository.existsBySku("SKU-001")).thenReturn(false);
      when(productRepository.save(any(Product.class)))
          .thenAnswer(
              inv -> {
                Product p = inv.getArgument(0);
                return Product.reconstitute(
                    ProductId.of(1L),
                    p.getName(),
                    p.getDescription(),
                    p.getSku(),
                    p.getUnitPrice(),
                    p.getStockQuantity(),
                    p.isActive(),
                    p.getCreatedAt(),
                    p.getUpdatedAt());
              });

      Product result = productService.createProduct(command);

      assertThat(result.getName()).isEqualTo("Widget");
      assertThat(result.getId()).isNotNull();
      verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("should reject duplicate SKU")
    void shouldRejectDuplicateSku() {
      var command =
          new CreateProductCommand("Widget", "A widget", "SKU-001", BigDecimal.TEN, 100);
      when(productRepository.existsBySku("SKU-001")).thenReturn(true);

      assertThatThrownBy(() -> productService.createProduct(command))
          .isInstanceOf(ProductSkuAlreadyExistsException.class);
    }
  }

  @Nested
  @DisplayName("When managing product BOM")
  class ManageBom {

    private Product existingProduct;

    @BeforeEach
    void setUp() {
      existingProduct =
          Product.create("Widget", null, "SKU-001", BigDecimal.TEN, 100);
    }

    @Test
    @DisplayName("should add material to product")
    void shouldAddMaterial() {
      when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
      when(rawMaterialRepository.existsById(RawMaterialId.of(1L))).thenReturn(true);
      when(productRepository.save(any(Product.class)))
          .thenAnswer(inv -> inv.getArgument(0));

      var command = new AddMaterialCommand(1L, new BigDecimal("5"));
      Product result =
          productService.addMaterialToProduct(ProductId.of(1L), command);

      assertThat(result.getMaterials()).hasSize(1);
      assertThat(result.getMaterials().get(0).rawMaterialId())
          .isEqualTo(RawMaterialId.of(1L));
    }

    @Test
    @DisplayName("should throw when adding material from non-existent raw material")
    void shouldThrowForNonExistentRawMaterial() {
      when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
      when(rawMaterialRepository.existsById(RawMaterialId.of(99L))).thenReturn(false);

      var command = new AddMaterialCommand(99L, new BigDecimal("5"));

      assertThatThrownBy(
              () -> productService.addMaterialToProduct(ProductId.of(1L), command))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Raw material not found");
    }

    @Test
    @DisplayName("should throw when product not found for BOM operation")
    void shouldThrowWhenProductNotFound() {
      when(productRepository.findById(any())).thenReturn(Optional.empty());

      var command = new AddMaterialCommand(1L, new BigDecimal("5"));

      assertThatThrownBy(
              () -> productService.addMaterialToProduct(ProductId.of(99L), command))
          .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("should remove material from product")
    void shouldRemoveMaterial() {
      existingProduct.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));
      when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
      when(productRepository.save(any(Product.class)))
          .thenAnswer(inv -> inv.getArgument(0));

      Product result =
          productService.removeMaterialFromProduct(ProductId.of(1L), 1L);

      assertThat(result.getMaterials()).isEmpty();
    }

    @Test
    @DisplayName("should update material quantity")
    void shouldUpdateMaterialQuantity() {
      existingProduct.addMaterial(RawMaterialId.of(1L), new BigDecimal("5"));
      when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
      when(productRepository.save(any(Product.class)))
          .thenAnswer(inv -> inv.getArgument(0));

      Product result =
          productService.updateMaterialQuantity(
              ProductId.of(1L), 1L, new BigDecimal("20"));

      assertThat(result.getMaterials().get(0).quantityRequired())
          .isEqualByComparingTo(new BigDecimal("20"));
    }
  }
}

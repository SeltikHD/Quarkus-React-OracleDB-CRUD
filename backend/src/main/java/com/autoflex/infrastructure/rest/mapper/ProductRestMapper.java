package com.autoflex.infrastructure.rest.mapper;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.port.in.ProductUseCase;
import com.autoflex.infrastructure.rest.dto.BillOfMaterialItemResponse;
import com.autoflex.infrastructure.rest.dto.ProductRequest;
import com.autoflex.infrastructure.rest.dto.ProductResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductRestMapper - Maps between REST DTOs and domain objects.
 *
 * <p>This mapper handles the translation layer between the REST API (HTTP world) and the domain
 * layer (business world).
 */
@ApplicationScoped
public class ProductRestMapper {

  /** Converts a domain Product to an API response DTO. */
  public ProductResponse toResponse(Product product) {
    if (product == null) {
      return null;
    }

    List<BillOfMaterialItemResponse> materials =
        product.getMaterials().stream()
            .map(
                bom ->
                    new BillOfMaterialItemResponse(
                        bom.rawMaterialId().value(), bom.quantityRequired()))
            .collect(Collectors.toList());

    return ProductResponse.builder()
        .id(product.getId() != null ? product.getId().value() : null)
        .name(product.getName())
        .description(product.getDescription())
        .sku(product.getSku())
        .unitPrice(product.getUnitPrice())
        .stockQuantity(product.getStockQuantity())
        .active(product.isActive())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .materials(materials)
        .build();
  }

  /** Converts an API request to a CreateProductCommand. */
  public ProductUseCase.CreateProductCommand toCreateCommand(ProductRequest request) {
    return new ProductUseCase.CreateProductCommand(
        request.getName(),
        request.getDescription(),
        request.getSku(),
        request.getUnitPrice(),
        request.getStockQuantity() != null ? request.getStockQuantity() : 0);
  }

  /** Converts an API request to an UpdateProductCommand. */
  public ProductUseCase.UpdateProductCommand toUpdateCommand(ProductRequest request) {
    return new ProductUseCase.UpdateProductCommand(
        request.getName(), request.getDescription(), request.getSku(), request.getUnitPrice());
  }
}

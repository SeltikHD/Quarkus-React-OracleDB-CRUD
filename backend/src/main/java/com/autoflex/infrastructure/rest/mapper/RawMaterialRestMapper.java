package com.autoflex.infrastructure.rest.mapper;

import com.autoflex.domain.model.rawmaterial.MeasurementUnit;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.port.in.RawMaterialUseCase;
import com.autoflex.infrastructure.rest.dto.RawMaterialRequest;
import com.autoflex.infrastructure.rest.dto.RawMaterialResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

/** Maps between REST DTOs and domain objects for raw materials. */
@ApplicationScoped
public class RawMaterialRestMapper {

  public RawMaterialResponse toResponse(RawMaterial rawMaterial) {
    if (rawMaterial == null) return null;
    return RawMaterialResponse.builder()
        .id(rawMaterial.getId() != null ? rawMaterial.getId().value() : null)
        .name(rawMaterial.getName())
        .description(rawMaterial.getDescription())
        .code(rawMaterial.getCode())
        .unit(rawMaterial.getUnit().name())
        .unitAbbreviation(rawMaterial.getUnit().getAbbreviation())
        .stockQuantity(rawMaterial.getStockQuantity())
        .unitCost(rawMaterial.getUnitCost())
        .active(rawMaterial.isActive())
        .createdAt(rawMaterial.getCreatedAt())
        .updatedAt(rawMaterial.getUpdatedAt())
        .build();
  }

  public RawMaterialUseCase.CreateRawMaterialCommand toCreateCommand(RawMaterialRequest request) {
    return new RawMaterialUseCase.CreateRawMaterialCommand(
        request.getName(),
        request.getDescription(),
        request.getCode(),
        MeasurementUnit.valueOf(request.getUnit()),
        request.getStockQuantity() != null ? request.getStockQuantity() : BigDecimal.ZERO,
        request.getUnitCost());
  }

  public RawMaterialUseCase.UpdateRawMaterialCommand toUpdateCommand(RawMaterialRequest request) {
    return new RawMaterialUseCase.UpdateRawMaterialCommand(
        request.getName(),
        request.getDescription(),
        request.getCode(),
        MeasurementUnit.valueOf(request.getUnit()),
        request.getUnitCost());
  }
}

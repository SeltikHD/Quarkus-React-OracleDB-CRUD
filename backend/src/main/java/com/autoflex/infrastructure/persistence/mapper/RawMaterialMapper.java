package com.autoflex.infrastructure.persistence.mapper;

import com.autoflex.domain.model.rawmaterial.MeasurementUnit;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.infrastructure.persistence.entity.RawMaterialJpaEntity;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Maps between domain RawMaterial and JPA RawMaterialJpaEntity.
 */
@ApplicationScoped
public class RawMaterialMapper {

	public RawMaterial toDomain(RawMaterialJpaEntity entity) {
		if (entity == null)
			return null;
		return RawMaterial.reconstitute(
				entity.getId() != null ? RawMaterialId.of(entity.getId()) : null,
				entity.getName(),
				entity.getDescription(),
				entity.getCode(),
				MeasurementUnit.valueOf(entity.getMeasurementUnit()),
				entity.getStockQuantity(),
				entity.getUnitCost(),
				entity.getActive() != null && entity.getActive(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}

	public RawMaterialJpaEntity toJpaEntity(RawMaterial rawMaterial) {
		if (rawMaterial == null)
			return null;
		RawMaterialJpaEntity entity = new RawMaterialJpaEntity();
		if (rawMaterial.getId() != null)
			entity.setId(rawMaterial.getId().value());
		entity.setName(rawMaterial.getName());
		entity.setDescription(rawMaterial.getDescription());
		entity.setCode(rawMaterial.getCode());
		entity.setMeasurementUnit(rawMaterial.getUnit().name());
		entity.setStockQuantity(rawMaterial.getStockQuantity());
		entity.setUnitCost(rawMaterial.getUnitCost());
		entity.setActive(rawMaterial.isActive());
		entity.setCreatedAt(rawMaterial.getCreatedAt());
		entity.setUpdatedAt(rawMaterial.getUpdatedAt());
		return entity;
	}
}

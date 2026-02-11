package com.autoflex.domain.port.in;

import com.autoflex.domain.model.rawmaterial.MeasurementUnit;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * RawMaterialUseCase - Input port defining raw material management operations.
 */
public interface RawMaterialUseCase {

	// COMMANDS
	RawMaterial createRawMaterial(CreateRawMaterialCommand command);

	RawMaterial updateRawMaterial(RawMaterialId id, UpdateRawMaterialCommand command);

	RawMaterial adjustStock(RawMaterialId id, BigDecimal delta);

	void deactivateRawMaterial(RawMaterialId id);

	void deleteRawMaterial(RawMaterialId id);

	// QUERIES
	RawMaterial getRawMaterialById(RawMaterialId id);

	Optional<RawMaterial> getRawMaterialByCode(String code);

	List<RawMaterial> listActiveRawMaterials();

	List<RawMaterial> listAllRawMaterials();

	List<RawMaterial> searchRawMaterials(String searchTerm);

	// COMMAND RECORDS
	record CreateRawMaterialCommand(
			String name,
			String description,
			String code,
			MeasurementUnit unit,
			BigDecimal stockQuantity,
			BigDecimal unitCost) {

		public CreateRawMaterialCommand {
			if (name == null || name.isBlank())
				throw new IllegalArgumentException("Raw material name is required");
			if (code == null || code.isBlank())
				throw new IllegalArgumentException("Raw material code is required");
			if (unit == null)
				throw new IllegalArgumentException("Measurement unit is required");
			if (unitCost == null)
				throw new IllegalArgumentException("Unit cost is required");
			if (stockQuantity == null)
				stockQuantity = BigDecimal.ZERO;
		}
	}

	record UpdateRawMaterialCommand(
			String name,
			String description,
			String code,
			MeasurementUnit unit,
			BigDecimal unitCost) {

		public UpdateRawMaterialCommand {
			if (name == null || name.isBlank())
				throw new IllegalArgumentException("Raw material name is required");
			if (code == null || code.isBlank())
				throw new IllegalArgumentException("Raw material code is required");
			if (unit == null)
				throw new IllegalArgumentException("Measurement unit is required");
			if (unitCost == null)
				throw new IllegalArgumentException("Unit cost is required");
		}
	}

	// DOMAIN EXCEPTIONS
	class RawMaterialNotFoundException extends RuntimeException {
		public RawMaterialNotFoundException(RawMaterialId id) {
			super("Raw material not found with ID: " + id);
		}
	}

	class RawMaterialCodeAlreadyExistsException extends RuntimeException {
		public RawMaterialCodeAlreadyExistsException(String code) {
			super("Raw material with code '" + code + "' already exists");
		}
	}
}

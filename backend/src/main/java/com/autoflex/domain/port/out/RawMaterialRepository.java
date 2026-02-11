package com.autoflex.domain.port.out;

import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.util.List;
import java.util.Optional;

/**
 * RawMaterialRepository - Output port for raw material persistence operations.
 */
public interface RawMaterialRepository {

	RawMaterial save(RawMaterial rawMaterial);

	Optional<RawMaterial> findById(RawMaterialId id);

	Optional<RawMaterial> findByCode(String code);

	List<RawMaterial> findAllActive();

	List<RawMaterial> findAll();

	List<RawMaterial> findByNameContaining(String name);

	List<RawMaterial> findByIds(List<RawMaterialId> ids);

	boolean deleteById(RawMaterialId id);

	boolean existsByCode(String code);

	boolean existsById(RawMaterialId id);
}

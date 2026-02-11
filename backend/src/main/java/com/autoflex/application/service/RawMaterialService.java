package com.autoflex.application.service;

import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.in.RawMaterialUseCase;
import com.autoflex.domain.port.out.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * RawMaterialService - Application service implementing RawMaterialUseCase.
 * Orchestrates domain logic and calls output ports.
 */
@ApplicationScoped
public class RawMaterialService implements RawMaterialUseCase {

	private final RawMaterialRepository rawMaterialRepository;

	@Inject
	public RawMaterialService(RawMaterialRepository rawMaterialRepository) {
		this.rawMaterialRepository = rawMaterialRepository;
	}

	@Override
	@Transactional
	public RawMaterial createRawMaterial(CreateRawMaterialCommand command) {
		if (rawMaterialRepository.existsByCode(command.code())) {
			throw new RawMaterialCodeAlreadyExistsException(command.code());
		}
		RawMaterial rawMaterial = RawMaterial.create(
				command.name(),
				command.description(),
				command.code(),
				command.unit(),
				command.stockQuantity(),
				command.unitCost());
		return rawMaterialRepository.save(rawMaterial);
	}

	@Override
	@Transactional
	public RawMaterial updateRawMaterial(RawMaterialId id, UpdateRawMaterialCommand command) {
		RawMaterial rawMaterial = findRawMaterialOrThrow(id);

		Optional<RawMaterial> existingWithCode = rawMaterialRepository.findByCode(command.code());
		if (existingWithCode.isPresent() && !existingWithCode.get().getId().equals(id)) {
			throw new RawMaterialCodeAlreadyExistsException(command.code());
		}

		rawMaterial.update(
				command.name(), command.description(), command.code(), command.unit(), command.unitCost());
		return rawMaterialRepository.save(rawMaterial);
	}

	@Override
	@Transactional
	public RawMaterial adjustStock(RawMaterialId id, BigDecimal delta) {
		RawMaterial rawMaterial = findRawMaterialOrThrow(id);
		rawMaterial.adjustStock(delta);
		return rawMaterialRepository.save(rawMaterial);
	}

	@Override
	@Transactional
	public void deactivateRawMaterial(RawMaterialId id) {
		RawMaterial rawMaterial = findRawMaterialOrThrow(id);
		rawMaterial.deactivate();
		rawMaterialRepository.save(rawMaterial);
	}

	@Override
	@Transactional
	public void deleteRawMaterial(RawMaterialId id) {
		if (!rawMaterialRepository.existsById(id)) {
			throw new RawMaterialNotFoundException(id);
		}
		rawMaterialRepository.deleteById(id);
	}

	@Override
	public RawMaterial getRawMaterialById(RawMaterialId id) {
		return findRawMaterialOrThrow(id);
	}

	@Override
	public Optional<RawMaterial> getRawMaterialByCode(String code) {
		return rawMaterialRepository.findByCode(code);
	}

	@Override
	public List<RawMaterial> listActiveRawMaterials() {
		return rawMaterialRepository.findAllActive();
	}

	@Override
	public List<RawMaterial> listAllRawMaterials() {
		return rawMaterialRepository.findAll();
	}

	@Override
	public List<RawMaterial> searchRawMaterials(String searchTerm) {
		if (searchTerm == null || searchTerm.isBlank()) {
			return rawMaterialRepository.findAllActive();
		}
		return rawMaterialRepository.findByNameContaining(searchTerm.trim());
	}

	private RawMaterial findRawMaterialOrThrow(RawMaterialId id) {
		return rawMaterialRepository
				.findById(id)
				.orElseThrow(() -> new RawMaterialNotFoundException(id));
	}
}

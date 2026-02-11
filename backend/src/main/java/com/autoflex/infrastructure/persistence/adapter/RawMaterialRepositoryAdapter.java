package com.autoflex.infrastructure.persistence.adapter;

import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.out.RawMaterialRepository;
import com.autoflex.infrastructure.persistence.entity.RawMaterialJpaEntity;
import com.autoflex.infrastructure.persistence.mapper.RawMaterialMapper;
import com.autoflex.infrastructure.persistence.repository.RawMaterialPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Output adapter implementing RawMaterialRepository using JPA/Panache. */
@ApplicationScoped
public class RawMaterialRepositoryAdapter implements RawMaterialRepository {

  private final RawMaterialPanacheRepository panacheRepository;
  private final RawMaterialMapper mapper;

  @Inject
  public RawMaterialRepositoryAdapter(
      RawMaterialPanacheRepository panacheRepository, RawMaterialMapper mapper) {
    this.panacheRepository = panacheRepository;
    this.mapper = mapper;
  }

  @Override
  public RawMaterial save(RawMaterial rawMaterial) {
    RawMaterialJpaEntity entity = mapper.toJpaEntity(rawMaterial);
    if (entity.getId() == null) {
      panacheRepository.persist(entity);
    } else {
      entity = panacheRepository.getEntityManager().merge(entity);
    }
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<RawMaterial> findById(RawMaterialId id) {
    return panacheRepository.findByIdOptional(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<RawMaterial> findByCode(String code) {
    return panacheRepository
        .find("code", code.toUpperCase())
        .firstResultOptional()
        .map(mapper::toDomain);
  }

  @Override
  public List<RawMaterial> findAllActive() {
    return panacheRepository.find("active", true).list().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<RawMaterial> findAll() {
    return panacheRepository.listAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<RawMaterial> findByNameContaining(String name) {
    return panacheRepository.find("LOWER(name) LIKE LOWER(?1)", "%" + name + "%").list().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<RawMaterial> findByIds(List<RawMaterialId> ids) {
    List<Long> longIds = ids.stream().map(RawMaterialId::value).collect(Collectors.toList());
    return panacheRepository.find("id IN ?1", longIds).list().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean deleteById(RawMaterialId id) {
    return panacheRepository.deleteById(id.value());
  }

  @Override
  public boolean existsByCode(String code) {
    return panacheRepository.count("code", code.toUpperCase()) > 0;
  }

  @Override
  public boolean existsById(RawMaterialId id) {
    return panacheRepository.findByIdOptional(id.value()).isPresent();
  }
}

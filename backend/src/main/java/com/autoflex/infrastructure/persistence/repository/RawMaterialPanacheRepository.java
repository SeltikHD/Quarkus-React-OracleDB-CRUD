package com.autoflex.infrastructure.persistence.repository;

import com.autoflex.infrastructure.persistence.entity.RawMaterialJpaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Panache repository for RawMaterial JPA entity.
 */
@ApplicationScoped
public class RawMaterialPanacheRepository implements PanacheRepository<RawMaterialJpaEntity> {
	// Panache provides standard CRUD operations
}

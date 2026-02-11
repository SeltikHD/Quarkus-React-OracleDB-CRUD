package com.autoflex.infrastructure.persistence.repository;

import com.autoflex.infrastructure.persistence.entity.ProductMaterialJpaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/** Panache repository for ProductMaterial JPA entity (bill of materials join table). */
@ApplicationScoped
public class ProductMaterialPanacheRepository
    implements PanacheRepository<ProductMaterialJpaEntity> {
  // Panache provides standard CRUD operations
}

package com.autoflex.infrastructure.persistence.repository;

import com.autoflex.infrastructure.persistence.entity.ProductJpaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * ProductPanacheRepository - Quarkus Panache repository for ProductJpaEntity.
 *
 * <p>
 * This is a thin wrapper around Panache that provides standard CRUD operations.
 * Complex queries are implemented in the adapter layer.
 *
 * <p>
 * <b>NOTE:</b> This is an infrastructure implementation detail.
 * The domain layer is unaware of Panache.
 */
@ApplicationScoped
public class ProductPanacheRepository implements PanacheRepository<ProductJpaEntity> {
    // Panache provides standard CRUD operations
    // Custom query methods can be added here if needed
}

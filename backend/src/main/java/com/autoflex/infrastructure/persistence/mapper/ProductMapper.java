package com.autoflex.infrastructure.persistence.mapper;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;
import com.autoflex.infrastructure.persistence.entity.ProductJpaEntity;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * ProductMapper - Maps between domain Product and JPA ProductJpaEntity.
 *
 * <p><b>HEXAGONAL ARCHITECTURE:</b>
 * This mapper is part of the infrastructure layer and handles the translation
 * between the pure domain model and the JPA persistence model.
 *
 * <p><b>KEY RESPONSIBILITY:</b>
 * Keep the domain model completely free of JPA annotations by providing
 * explicit mapping logic here.
 */
@ApplicationScoped
public class ProductMapper {

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity the JPA entity from database
     * @return the domain entity for business logic
     */
    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Product.reconstitute(
                entity.getId() != null ? ProductId.of(entity.getId()) : null,
                entity.getName(),
                entity.getDescription(),
                entity.getSku(),
                entity.getUnitPrice(),
                entity.getStockQuantity(),
                entity.getActive() != null && entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Converts a domain entity to a JPA entity.
     *
     * @param product the domain entity
     * @return the JPA entity for persistence
     */
    public ProductJpaEntity toJpaEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductJpaEntity entity = new ProductJpaEntity();
        
        // Only set ID if the product has been persisted before
        if (product.getId() != null) {
            entity.setId(product.getId().value());
        }
        
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setSku(product.getSku());
        entity.setUnitPrice(product.getUnitPrice());
        entity.setStockQuantity(product.getStockQuantity());
        entity.setActive(product.isActive());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());

        return entity;
    }
}

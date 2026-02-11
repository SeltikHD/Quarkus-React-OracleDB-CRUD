package com.autoflex.infrastructure.persistence.adapter;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;
import com.autoflex.domain.port.out.ProductRepository;
import com.autoflex.infrastructure.persistence.entity.ProductJpaEntity;
import com.autoflex.infrastructure.persistence.entity.ProductMaterialJpaEntity;
import com.autoflex.infrastructure.persistence.mapper.ProductMapper;
import com.autoflex.infrastructure.persistence.repository.ProductPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductRepositoryAdapter - Infrastructure adapter implementing the domain's ProductRepository
 * port.
 *
 * <p><b>HEXAGONAL ARCHITECTURE:</b> This is an OUTPUT ADAPTER (also called "driven adapter" or
 * "secondary adapter"). It implements the domain's output port using infrastructure technology
 * (JPA/Panache).
 *
 * <p><b>RESPONSIBILITIES:</b>
 *
 * <ul>
 *   <li>Implement the domain's repository interface
 *   <li>Map between domain entities and JPA entities
 *   <li>Handle JPA-specific concerns (transactions are managed by the service layer)
 * </ul>
 */
@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepository {

  private final ProductPanacheRepository panacheRepository;
  private final ProductMapper mapper;

  @Inject
  public ProductRepositoryAdapter(
      ProductPanacheRepository panacheRepository, ProductMapper mapper) {
    this.panacheRepository = panacheRepository;
    this.mapper = mapper;
  }

  @Override
  public Product save(Product product) {
    ProductJpaEntity entity;

    if (product.getId() == null) {
      // New entity - create from mapper and persist
      entity = mapper.toJpaEntity(product);
      panacheRepository.persist(entity);
    } else {
      // Existing entity - find the managed instance and update in place
      entity = panacheRepository.findById(product.getId().value());
      if (entity == null) {
        throw new IllegalStateException("Product not found for update: " + product.getId().value());
      }

      // Update scalar fields on the managed entity
      entity.setName(product.getName());
      entity.setDescription(product.getDescription());
      entity.setSku(product.getSku());
      entity.setUnitPrice(product.getUnitPrice());
      entity.setStockQuantity(product.getStockQuantity());
      entity.setActive(product.isActive());
      entity.setUpdatedAt(product.getUpdatedAt());

      // Sync materials collection: clear existing and re-add from domain model.
      // orphanRemoval = true ensures removed children are deleted from DB.
      entity.getMaterials().clear();
      panacheRepository.getEntityManager().flush();

      product
          .getMaterials()
          .forEach(
              bom -> {
                ProductMaterialJpaEntity materialEntity =
                    new ProductMaterialJpaEntity(
                        entity, bom.rawMaterialId().value(), bom.quantityRequired());
                entity.getMaterials().add(materialEntity);
              });
    }

    return mapper.toDomain(entity);
  }

  @Override
  public Optional<Product> findById(ProductId id) {
    return panacheRepository.findByIdOptional(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findBySku(String sku) {
    return panacheRepository
        .find("sku", sku.toUpperCase())
        .firstResultOptional()
        .map(mapper::toDomain);
  }

  @Override
  public List<Product> findAllActive() {
    return panacheRepository.find("active", true).list().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findAll() {
    return panacheRepository.listAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Product> findByNameContaining(String name) {
    return panacheRepository.find("LOWER(name) LIKE LOWER(?1)", "%" + name + "%").list().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean deleteById(ProductId id) {
    return panacheRepository.deleteById(id.value());
  }

  @Override
  public boolean existsBySku(String sku) {
    return panacheRepository.count("sku", sku.toUpperCase()) > 0;
  }

  @Override
  public boolean existsById(ProductId id) {
    return panacheRepository.findByIdOptional(id.value()).isPresent();
  }

  @Override
  public List<Product> findAllActiveWithMaterials() {
    // Use a fetch join to eagerly load materials and avoid N+1 queries
    return panacheRepository
        .getEntityManager()
        .createQuery(
            "SELECT DISTINCT p FROM ProductJpaEntity p "
                + "LEFT JOIN FETCH p.materials "
                + "WHERE p.active = true",
            ProductJpaEntity.class)
        .getResultList()
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }
}

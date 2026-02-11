package com.autoflex.application.service;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.production.ProductionPlan;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.port.in.ProductionUseCase;
import com.autoflex.domain.port.out.ProductRepository;
import com.autoflex.domain.port.out.RawMaterialRepository;
import com.autoflex.domain.service.ProductionCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 * ProductionService - Application service implementing ProductionUseCase. Coordinates between
 * Product and RawMaterial repositories to run the Greedy Production Calculation Algorithm.
 */
@ApplicationScoped
public class ProductionService implements ProductionUseCase {

  private final ProductRepository productRepository;
  private final RawMaterialRepository rawMaterialRepository;

  @Inject
  public ProductionService(
      ProductRepository productRepository, RawMaterialRepository rawMaterialRepository) {
    this.productRepository = productRepository;
    this.rawMaterialRepository = rawMaterialRepository;
  }

  @Override
  public ProductionPlan calculateProductionPlan() {
    List<Product> activeProducts = productRepository.findAllActiveWithMaterials();
    List<RawMaterial> activeRawMaterials = rawMaterialRepository.findAllActive();
    return ProductionCalculator.calculate(activeProducts, activeRawMaterials);
  }
}

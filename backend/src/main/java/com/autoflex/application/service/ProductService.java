package com.autoflex.application.service;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.in.ProductUseCase;
import com.autoflex.domain.port.out.ProductRepository;
import com.autoflex.domain.port.out.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ProductService - Application Service implementing the ProductUseCase.
 *
 * <p><b>HEXAGONAL ARCHITECTURE:</b> This class is the "Application Layer" or "Use Case Layer". It
 * orchestrates the domain logic and coordinates between ports.
 *
 * <p><b>RESPONSIBILITIES:</b>
 *
 * <ul>
 *   <li>Transaction management
 *   <li>Use case orchestration
 *   <li>Calling domain entities for business logic
 *   <li>Calling output ports (repositories)
 * </ul>
 *
 * <p><b>NOTE:</b> This layer CAN have framework annotations (@ApplicationScoped, @Transactional)
 * because it's an adapter connecting the domain to the infrastructure. The domain entities
 * themselves remain pure.
 */
@ApplicationScoped
public class ProductService implements ProductUseCase {

  private final ProductRepository productRepository;
  private final RawMaterialRepository rawMaterialRepository;

  @Inject
  public ProductService(
      ProductRepository productRepository, RawMaterialRepository rawMaterialRepository) {
    this.productRepository = productRepository;
    this.rawMaterialRepository = rawMaterialRepository;
  }

  // =========================================================================
  // COMMANDS
  // =========================================================================

  @Override
  @Transactional
  public Product createProduct(CreateProductCommand command) {
    // Check if SKU already exists
    if (productRepository.existsBySku(command.sku())) {
      throw new ProductSkuAlreadyExistsException(command.sku());
    }

    // Create domain entity (business validation happens here)
    Product product =
        Product.create(
            command.name(),
            command.description(),
            command.sku(),
            command.unitPrice(),
            command.stockQuantity());

    // Persist and return
    return productRepository.save(product);
  }

  @Override
  @Transactional
  public Product updateProduct(ProductId id, UpdateProductCommand command) {
    // Find existing product
    Product product = findProductOrThrow(id);

    // Check if new SKU conflicts with another product
    Optional<Product> existingWithSku = productRepository.findBySku(command.sku());
    if (existingWithSku.isPresent() && !existingWithSku.get().getId().equals(id)) {
      throw new ProductSkuAlreadyExistsException(command.sku());
    }

    // Update using domain method (business validation happens here)
    product.update(command.name(), command.description(), command.sku(), command.unitPrice());

    // Persist and return
    return productRepository.save(product);
  }

  @Override
  @Transactional
  public Product adjustStock(ProductId id, int quantityDelta) {
    Product product = findProductOrThrow(id);

    // Check if reduction would result in negative stock
    if (quantityDelta < 0 && !product.hasSufficientStock(Math.abs(quantityDelta))) {
      throw new InsufficientStockException(id, product.getStockQuantity(), Math.abs(quantityDelta));
    }

    // Adjust stock using domain method
    product.adjustStock(quantityDelta);

    return productRepository.save(product);
  }

  @Override
  @Transactional
  public void deactivateProduct(ProductId id) {
    Product product = findProductOrThrow(id);
    product.deactivate();
    productRepository.save(product);
  }

  @Override
  @Transactional
  public void deleteProduct(ProductId id) {
    if (!productRepository.existsById(id)) {
      throw new ProductNotFoundException(id);
    }
    productRepository.deleteById(id);
  }

  // =========================================================================
  // QUERIES
  // =========================================================================

  @Override
  public Product getProductById(ProductId id) {
    return findProductOrThrow(id);
  }

  @Override
  public Optional<Product> getProductBySku(String sku) {
    return productRepository.findBySku(sku);
  }

  @Override
  public List<Product> listActiveProducts() {
    return productRepository.findAllActive();
  }

  @Override
  public List<Product> listAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public List<Product> searchProducts(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      return productRepository.findAllActive();
    }
    return productRepository.findByNameContaining(searchTerm.trim());
  }

  // =========================================================================
  // HELPER METHODS
  // =========================================================================

  private Product findProductOrThrow(ProductId id) {
    return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
  }

  // =========================================================================
  // BILL OF MATERIALS MANAGEMENT
  // =========================================================================

  @Override
  @Transactional
  public Product addMaterialToProduct(ProductId productId, AddMaterialCommand command) {
    Product product = findProductOrThrow(productId);

    // Validate that the raw material exists
    RawMaterialId rawMaterialId = RawMaterialId.of(command.rawMaterialId());
    if (!rawMaterialRepository.existsById(rawMaterialId)) {
      throw new IllegalArgumentException(
          "Raw material not found with ID: " + command.rawMaterialId());
    }

    product.addMaterial(rawMaterialId, command.quantityRequired());
    return productRepository.save(product);
  }

  @Override
  @Transactional
  public Product removeMaterialFromProduct(ProductId productId, Long rawMaterialId) {
    Product product = findProductOrThrow(productId);
    product.removeMaterial(RawMaterialId.of(rawMaterialId));
    return productRepository.save(product);
  }

  @Override
  @Transactional
  public Product updateMaterialQuantity(
      ProductId productId, Long rawMaterialId, BigDecimal newQuantity) {
    Product product = findProductOrThrow(productId);
    product.updateMaterialQuantity(RawMaterialId.of(rawMaterialId), newQuantity);
    return productRepository.save(product);
  }
}

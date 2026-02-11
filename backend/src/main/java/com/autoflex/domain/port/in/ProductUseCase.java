package com.autoflex.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;

/**
 * ProductUseCase - Input Port defining product management operations.
 *
 * <p>
 * <b>HEXAGONAL ARCHITECTURE:</b>
 * This is an INPUT PORT (also called a "driving port" or "primary port").
 * It defines the use cases that the application exposes to the outside world.
 *
 * <p>
 * The REST API (adapter) will call these methods. The implementation
 * (in the application layer) will orchestrate domain logic and call output
 * ports.
 *
 * <p>
 * <b>DESIGN NOTES:</b>
 * <ul>
 * <li>Uses Command/Query objects for complex inputs (CQRS-inspired)</li>
 * <li>Returns domain objects, not DTOs</li>
 * <li>Throws domain exceptions for known error cases</li>
 * </ul>
 */
public interface ProductUseCase {

    // =========================================================================
    // COMMANDS (Write operations)
    // =========================================================================

    /**
     * Creates a new product in the system.
     *
     * @param command the product creation data
     * @return the created product with generated ID
     * @throws ProductSkuAlreadyExistsException if SKU is already in use
     */
    Product createProduct(CreateProductCommand command);

    /**
     * Updates an existing product.
     *
     * @param id      the product ID to update
     * @param command the updated product data
     * @return the updated product
     * @throws ProductNotFoundException         if product doesn't exist
     * @throws ProductSkuAlreadyExistsException if new SKU is already in use
     */
    Product updateProduct(ProductId id, UpdateProductCommand command);

    /**
     * Adjusts the stock quantity of a product.
     *
     * @param id            the product ID
     * @param quantityDelta positive to add, negative to subtract
     * @return the updated product
     * @throws ProductNotFoundException   if product doesn't exist
     * @throws InsufficientStockException if reducing below zero
     */
    Product adjustStock(ProductId id, int quantityDelta);

    /**
     * Deactivates a product (soft delete).
     *
     * @param id the product ID to deactivate
     * @throws ProductNotFoundException if product doesn't exist
     */
    void deactivateProduct(ProductId id);

    /**
     * Permanently deletes a product.
     *
     * @param id the product ID to delete
     * @throws ProductNotFoundException if product doesn't exist
     */
    void deleteProduct(ProductId id);

    // =========================================================================
    // QUERIES (Read operations)
    // =========================================================================

    /**
     * Retrieves a product by its ID.
     *
     * @param id the product ID
     * @return the product if found
     * @throws ProductNotFoundException if product doesn't exist
     */
    Product getProductById(ProductId id);

    /**
     * Retrieves a product by its SKU.
     *
     * @param sku the product SKU
     * @return the product if found, empty otherwise
     */
    Optional<Product> getProductBySku(String sku);

    /**
     * Lists all active products.
     *
     * @return list of active products
     */
    List<Product> listActiveProducts();

    /**
     * Lists all products including inactive ones.
     *
     * @return list of all products
     */
    List<Product> listAllProducts();

    /**
     * Searches products by name.
     *
     * @param searchTerm the search term
     * @return matching products
     */
    List<Product> searchProducts(String searchTerm);

    // =========================================================================
    // BILL OF MATERIALS MANAGEMENT
    // =========================================================================

    /**
     * Adds a raw material to a product's bill of materials.
     *
     * @param productId the product ID
     * @param command   the material and quantity to add
     * @return the updated product
     * @throws ProductNotFoundException if product doesn't exist
     */
    Product addMaterialToProduct(ProductId productId, AddMaterialCommand command);

    /**
     * Removes a raw material from a product's bill of materials.
     *
     * @param productId     the product ID
     * @param rawMaterialId the raw material ID to remove
     * @return the updated product
     * @throws ProductNotFoundException if product doesn't exist
     */
    Product removeMaterialFromProduct(ProductId productId, Long rawMaterialId);

    /**
     * Updates the required quantity of a raw material in a product's BOM.
     *
     * @param productId     the product ID
     * @param rawMaterialId the raw material ID to update
     * @param newQuantity   the new required quantity
     * @return the updated product
     * @throws ProductNotFoundException if product doesn't exist
     */
    Product updateMaterialQuantity(ProductId productId, Long rawMaterialId, BigDecimal newQuantity);

    // =========================================================================
    // COMMAND RECORDS (Immutable input objects)
    // =========================================================================

    /**
     * Command for creating a new product.
     */
    record CreateProductCommand(
            String name,
            String description,
            String sku,
            BigDecimal unitPrice,
            Integer stockQuantity) {
        public CreateProductCommand {
            // Basic null checks - domain entity handles full validation
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Product name is required");
            }
            if (sku == null || sku.isBlank()) {
                throw new IllegalArgumentException("Product SKU is required");
            }
            if (unitPrice == null) {
                throw new IllegalArgumentException("Unit price is required");
            }
            if (stockQuantity == null) {
                stockQuantity = 0;
            }
        }
    }

    /**
     * Command for updating an existing product.
     */
    record UpdateProductCommand(
            String name,
            String description,
            String sku,
            BigDecimal unitPrice) {
        public UpdateProductCommand {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Product name is required");
            }
            if (sku == null || sku.isBlank()) {
                throw new IllegalArgumentException("Product SKU is required");
            }
            if (unitPrice == null) {
                throw new IllegalArgumentException("Unit price is required");
            }
        }
    }

    /**
     * Command for adding a raw material to a product's bill of materials.
     */
    record AddMaterialCommand(Long rawMaterialId, BigDecimal quantityRequired) {
        public AddMaterialCommand {
            if (rawMaterialId == null) {
                throw new IllegalArgumentException("Raw material ID is required");
            }
            if (quantityRequired == null) {
                throw new IllegalArgumentException("Quantity required is required");
            }
            if (quantityRequired.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Quantity required must be positive");
            }
        }
    }

    // =========================================================================
    // DOMAIN EXCEPTIONS
    // =========================================================================

    class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(ProductId id) {
            super("Product not found with ID: " + id);
        }
    }

    class ProductSkuAlreadyExistsException extends RuntimeException {
        public ProductSkuAlreadyExistsException(String sku) {
            super("Product with SKU '" + sku + "' already exists");
        }
    }

    class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(ProductId id, int available, int requested) {
            super("Insufficient stock for product " + id +
                    ". Available: " + available + ", Requested: " + requested);
        }
    }
}

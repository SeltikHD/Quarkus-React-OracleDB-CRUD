package com.autoflex.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.autoflex.domain.model.product.Product;
import com.autoflex.domain.model.product.ProductId;

/**
 * ProductRepository - Output Port for Product persistence operations.
 *
 * <p>
 * This interface defines the contract that any persistence adapter must
 * implement.
 * It is part of the domain layer but will be implemented in the infrastructure
 * layer.
 *
 * <p>
 * <b>HEXAGONAL ARCHITECTURE:</b>
 * This is an OUTPUT PORT (also called a "driven port" or "secondary port").
 * The domain layer defines what it needs, and the infrastructure provides the
 * implementation.
 *
 * <p>
 * <b>KEY PRINCIPLES:</b>
 * <ul>
 * <li>Uses domain objects (Product, ProductId), not JPA entities</li>
 * <li>No framework-specific types (no Page, Pageable, etc.)</li>
 * <li>Returns Optional for single queries (null-safe)</li>
 * <li>Throws domain exceptions, not infrastructure exceptions</li>
 * </ul>
 */
public interface ProductRepository {

    /**
     * Saves a product to the persistent store.
     * If the product has no ID, it will be created. Otherwise, it will be updated.
     *
     * @param product the product to save
     * @return the saved product with generated ID (if new)
     */
    Product save(Product product);

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the product ID
     * @return an Optional containing the product if found, empty otherwise
     */
    Optional<Product> findById(ProductId id);

    /**
     * Finds a product by its SKU (Stock Keeping Unit).
     * SKU should be unique across all products.
     *
     * @param sku the product SKU
     * @return an Optional containing the product if found, empty otherwise
     */
    Optional<Product> findBySku(String sku);

    /**
     * Retrieves all active products.
     *
     * @return a list of all active products
     */
    List<Product> findAllActive();

    /**
     * Retrieves all products (including inactive).
     *
     * @return a list of all products
     */
    List<Product> findAll();

    /**
     * Searches for products by name (partial match, case-insensitive).
     *
     * @param name the name pattern to search for
     * @return a list of matching products
     */
    List<Product> findByNameContaining(String name);

    /**
     * Deletes a product by its identifier.
     * Note: Consider using soft delete (deactivate) instead for audit trails.
     *
     * @param id the product ID to delete
     * @return true if the product was deleted, false if not found
     */
    boolean deleteById(ProductId id);

    /**
     * Checks if a product with the given SKU already exists.
     *
     * @param sku the SKU to check
     * @return true if a product with this SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Checks if a product with the given ID exists.
     *
     * @param id the product ID to check
     * @return true if the product exists
     */
    boolean existsById(ProductId id);

    /**
     * Retrieves all active products with their bill of materials eagerly loaded.
     * Used by the production calculator to avoid N+1 queries.
     *
     * @return a list of active products with materials populated
     */
    List<Product> findAllActiveWithMaterials();
}

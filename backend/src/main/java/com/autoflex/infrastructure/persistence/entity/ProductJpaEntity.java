package com.autoflex.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * ProductJpaEntity - JPA Entity for Product persistence.
 *
 * <p>
 * <b>HEXAGONAL ARCHITECTURE:</b>
 * This is an INFRASTRUCTURE concern, NOT a domain entity.
 * It exists purely to map to the database schema.
 *
 * <p>
 * <b>IMPORTANT DISTINCTION:</b>
 * <ul>
 * <li>{@code Product} (domain) - Pure business logic, no annotations</li>
 * <li>{@code ProductJpaEntity} (infrastructure) - JPA mapping, no business
 * logic</li>
 * </ul>
 *
 * <p>
 * The repository adapter handles conversion between these two classes.
 */
@Entity
@Table(name = "PRODUCTS")
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 255)
    private String name;

    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name = "SKU", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "STOCK_QUANTITY", nullable = false)
    private Integer stockQuantity;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductMaterialJpaEntity> materials = new ArrayList<>();

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    public ProductJpaEntity() {
        // Required by JPA
    }

    // =========================================================================
    // GETTERS AND SETTERS (Required by JPA)
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProductMaterialJpaEntity> getMaterials() {
        return materials;
    }

    public void setMaterials(List<ProductMaterialJpaEntity> materials) {
        this.materials = materials;
    }
}

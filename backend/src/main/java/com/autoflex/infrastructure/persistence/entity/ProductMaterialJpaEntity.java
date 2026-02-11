package com.autoflex.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

/**
 * JPA entity representing the association between a Product and a RawMaterial in the bill of
 * materials (BOM). Each row describes how much of a specific raw material is needed to produce one
 * unit of a product.
 */
@Entity
@Table(
    name = "PRODUCT_MATERIALS",
    uniqueConstraints =
        @UniqueConstraint(
            columnNames = {"PRODUCT_ID", "RAW_MATERIAL_ID"},
            name = "UK_PRODUCT_MATERIAL"))
public class ProductMaterialJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_material_seq")
  @SequenceGenerator(
      name = "product_material_seq",
      sequenceName = "PRODUCT_MATERIAL_SEQ",
      allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PRODUCT_ID", nullable = false)
  private ProductJpaEntity product;

  @Column(name = "RAW_MATERIAL_ID", nullable = false)
  private Long rawMaterialId;

  @Column(name = "QUANTITY_REQUIRED", nullable = false, precision = 19, scale = 4)
  private BigDecimal quantityRequired;

  public ProductMaterialJpaEntity() {}

  public ProductMaterialJpaEntity(
      ProductJpaEntity product, Long rawMaterialId, BigDecimal quantityRequired) {
    this.product = product;
    this.rawMaterialId = rawMaterialId;
    this.quantityRequired = quantityRequired;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ProductJpaEntity getProduct() {
    return product;
  }

  public void setProduct(ProductJpaEntity product) {
    this.product = product;
  }

  public Long getRawMaterialId() {
    return rawMaterialId;
  }

  public void setRawMaterialId(Long rawMaterialId) {
    this.rawMaterialId = rawMaterialId;
  }

  public BigDecimal getQuantityRequired() {
    return quantityRequired;
  }

  public void setQuantityRequired(BigDecimal quantityRequired) {
    this.quantityRequired = quantityRequired;
  }
}

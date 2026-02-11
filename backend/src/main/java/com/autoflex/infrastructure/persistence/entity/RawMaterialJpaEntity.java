package com.autoflex.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** JPA entity representing a raw material in the RAWS_MATERIALS table. */
@Entity
@Table(name = "RAW_MATERIALS")
public class RawMaterialJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "raw_material_seq")
  @SequenceGenerator(
      name = "raw_material_seq",
      sequenceName = "RAW_MATERIAL_SEQ",
      allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME", nullable = false, length = 255)
  private String name;

  @Column(name = "DESCRIPTION", length = 2000)
  private String description;

  @Column(name = "CODE", nullable = false, unique = true, length = 50)
  private String code;

  @Column(name = "MEASUREMENT_UNIT", nullable = false, length = 20)
  private String measurementUnit;

  @Column(name = "STOCK_QUANTITY", nullable = false, precision = 19, scale = 4)
  private BigDecimal stockQuantity;

  @Column(name = "UNIT_COST", nullable = false, precision = 19, scale = 4)
  private BigDecimal unitCost;

  @Column(name = "ACTIVE", nullable = false)
  private Boolean active;

  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "UPDATED_AT", nullable = false)
  private LocalDateTime updatedAt;

  public RawMaterialJpaEntity() {}

  // Getters and Setters
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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(String measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  public BigDecimal getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(BigDecimal stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
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
}

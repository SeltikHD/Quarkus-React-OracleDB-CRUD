package com.autoflex.domain.model.rawmaterial;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * RawMaterial - Domain entity representing a raw material used in product
 * manufacturing.
 *
 * <p>
 * This is a pure domain entity with NO framework annotations.
 * Stock quantities use BigDecimal to support fractional units (e.g., 0.5 kg).
 */
public class RawMaterial {

	private final RawMaterialId id;
	private String name;
	private String description;
	private String code;
	private MeasurementUnit unit;
	private BigDecimal stockQuantity;
	private BigDecimal unitCost;
	private boolean active;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private RawMaterial(
			RawMaterialId id,
			String name,
			String description,
			String code,
			MeasurementUnit unit,
			BigDecimal stockQuantity,
			BigDecimal unitCost,
			boolean active,
			LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.code = code;
		this.unit = unit;
		this.stockQuantity = stockQuantity;
		this.unitCost = unitCost;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * Factory method for creating a new raw material.
	 */
	public static RawMaterial create(
			String name,
			String description,
			String code,
			MeasurementUnit unit,
			BigDecimal stockQuantity,
			BigDecimal unitCost) {
		validateName(name);
		validateCode(code);
		Objects.requireNonNull(unit, "Measurement unit cannot be null");
		validateStockQuantity(stockQuantity);
		validateUnitCost(unitCost);
		LocalDateTime now = LocalDateTime.now();
		return new RawMaterial(
				null,
				name.trim(),
				description != null ? description.trim() : null,
				code.toUpperCase().trim(),
				unit,
				stockQuantity,
				unitCost,
				true,
				now,
				now);
	}

	/**
	 * Factory method for reconstituting a raw material from persistence.
	 */
	public static RawMaterial reconstitute(
			RawMaterialId id,
			String name,
			String description,
			String code,
			MeasurementUnit unit,
			BigDecimal stockQuantity,
			BigDecimal unitCost,
			boolean active,
			LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		return new RawMaterial(
				id, name, description, code, unit, stockQuantity, unitCost, active, createdAt, updatedAt);
	}

	/** Updates the raw material information. */
	public void update(
			String name,
			String description,
			String code,
			MeasurementUnit unit,
			BigDecimal unitCost) {
		validateName(name);
		validateCode(code);
		Objects.requireNonNull(unit, "Measurement unit cannot be null");
		validateUnitCost(unitCost);
		this.name = name.trim();
		this.description = description != null ? description.trim() : null;
		this.code = code.toUpperCase().trim();
		this.unit = unit;
		this.unitCost = unitCost;
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * Adjusts the stock quantity by the given delta.
	 *
	 * @param delta positive value to add stock, negative to consume stock
	 * @throws IllegalArgumentException if the resulting stock would be negative
	 */
	public void adjustStock(BigDecimal delta) {
		Objects.requireNonNull(delta, "Stock delta cannot be null");
		BigDecimal newQuantity = this.stockQuantity.add(delta);
		if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(
					"Cannot reduce stock below zero. Current: "
							+ stockQuantity
							+ " "
							+ unit.getAbbreviation()
							+ ", Delta: "
							+ delta);
		}
		this.stockQuantity = newQuantity;
		this.updatedAt = LocalDateTime.now();
	}

	/** Checks whether sufficient stock is available. */
	public boolean hasSufficientStock(BigDecimal requiredQuantity) {
		Objects.requireNonNull(requiredQuantity, "Required quantity cannot be null");
		return this.stockQuantity.compareTo(requiredQuantity) >= 0;
	}

	public void deactivate() {
		this.active = false;
		this.updatedAt = LocalDateTime.now();
	}

	public void activate() {
		this.active = true;
		this.updatedAt = LocalDateTime.now();
	}

	// --- Validation methods ---

	private static void validateName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Raw material name cannot be null or empty");
		}
		if (name.trim().length() > 255) {
			throw new IllegalArgumentException("Raw material name cannot exceed 255 characters");
		}
	}

	private static void validateCode(String code) {
		if (code == null || code.trim().isEmpty()) {
			throw new IllegalArgumentException("Raw material code cannot be null or empty");
		}
		if (!code.matches("^[A-Za-z0-9-]+$")) {
			throw new IllegalArgumentException(
					"Raw material code can only contain letters, numbers, and hyphens");
		}
	}

	private static void validateStockQuantity(BigDecimal quantity) {
		if (quantity == null) {
			throw new IllegalArgumentException("Stock quantity cannot be null");
		}
		if (quantity.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Stock quantity cannot be negative");
		}
	}

	private static void validateUnitCost(BigDecimal unitCost) {
		if (unitCost == null) {
			throw new IllegalArgumentException("Unit cost cannot be null");
		}
		if (unitCost.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Unit cost cannot be negative");
		}
	}

	// --- Getters ---

	public RawMaterialId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCode() {
		return code;
	}

	public MeasurementUnit getUnit() {
		return unit;
	}

	public BigDecimal getStockQuantity() {
		return stockQuantity;
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}

	public boolean isActive() {
		return active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RawMaterial that = (RawMaterial) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "RawMaterial{id="
				+ id
				+ ", name='"
				+ name
				+ "', code='"
				+ code
				+ "', unit="
				+ unit
				+ ", stock="
				+ stockQuantity
				+ ", active="
				+ active
				+ "}";
	}
}

package com.autoflex.domain.model.product;

import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * BillOfMaterialItem - Value Object representing the quantity of a raw material
 * required to produce one unit of a product.
 *
 * <p>
 * This is part of the Product aggregate's Bill of Materials (BOM).
 */
public record BillOfMaterialItem(RawMaterialId rawMaterialId, BigDecimal quantityRequired) {

	public BillOfMaterialItem {
		Objects.requireNonNull(rawMaterialId, "Raw material ID cannot be null");
		Objects.requireNonNull(quantityRequired, "Quantity required cannot be null");
		if (quantityRequired.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Quantity required must be positive");
		}
	}

	/**
	 * Creates a new BillOfMaterialItem.
	 *
	 * @param rawMaterialId    the raw material identifier
	 * @param quantityRequired the quantity needed to produce one unit of the
	 *                         product
	 * @return a new BillOfMaterialItem
	 */
	public static BillOfMaterialItem of(RawMaterialId rawMaterialId, BigDecimal quantityRequired) {
		return new BillOfMaterialItem(rawMaterialId, quantityRequired);
	}

	/**
	 * Creates a copy of this item with an updated quantity.
	 *
	 * @param newQuantity the new quantity required
	 * @return a new BillOfMaterialItem with the updated quantity
	 */
	public BillOfMaterialItem withQuantity(BigDecimal newQuantity) {
		return new BillOfMaterialItem(this.rawMaterialId, newQuantity);
	}
}

/**
 * Autoflex ERP - Central Type Definitions.
 *
 * All interfaces mirror the backend API DTOs for type safety.
 * Re-exports all domain types from a single entry point.
 */

// ============================================================================
// PRODUCT TYPES
// ============================================================================

/**
 * Product entity returned from the API.
 */
export interface IProduct {
  id: number;
  name: string;
  description: string | null;
  sku: string;
  unitPrice: number;
  stockQuantity: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  materials: IBillOfMaterialItem[];
}

/**
 * Request body for creating or updating a product.
 */
export interface IProductRequest {
  name: string;
  description?: string;
  sku: string;
  unitPrice: number;
  stockQuantity?: number;
}

// ============================================================================
// RAW MATERIAL TYPES
// ============================================================================

/**
 * Raw material entity returned from the API.
 */
export interface IRawMaterial {
  id: number;
  name: string;
  description: string | null;
  code: string;
  unit: string;
  unitAbbreviation: string;
  stockQuantity: number;
  unitCost: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request body for creating or updating a raw material.
 */
export interface IRawMaterialRequest {
  name: string;
  description?: string;
  code: string;
  unit: string;
  unitCost: number;
  stockQuantity?: number;
}

/**
 * Request body for adjusting raw material stock.
 */
export interface IStockAdjustmentRequest {
  quantity: number;
}

// ============================================================================
// BILL OF MATERIALS TYPES
// ============================================================================

/**
 * BOM item returned from the API.
 */
export interface IBillOfMaterialItem {
  rawMaterialId: number;
  quantityRequired: number;
}

/**
 * Request to add a material to the BOM.
 */
export interface IBillOfMaterialItemRequest {
  rawMaterialId: number;
  quantityRequired: number;
}

/**
 * Request to update material quantity in BOM.
 */
export interface IUpdateMaterialQuantityRequest {
  quantityRequired: number;
}

// ============================================================================
// PRODUCTION TYPES
// ============================================================================

/**
 * A single item in the production plan.
 */
export interface IProductionItem {
  productId: number;
  productName: string;
  productSku: string;
  quantity: number;
  unitPrice: number;
  totalValue: number;
}

/**
 * The full production plan returned by the greedy algorithm.
 */
export interface IProductionPlan {
  items: IProductionItem[];
  totalProductionValue: number;
  totalUnits: number;
  remainingStock: Record<string, number>;
}

// ============================================================================
// COMMON TYPES
// ============================================================================

/**
 * API error response structure.
 */
export interface IApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}

/**
 * Available measurement units matching the backend enum.
 */
export const MEASUREMENT_UNITS = [
  'KILOGRAM',
  'GRAM',
  'LITER',
  'MILLILITER',
  'METER',
  'CENTIMETER',
  'UNIT',
  'PIECE',
  'PAIR',
  'BOX',
  'ROLL',
  'SHEET',
] as const;

export type MeasurementUnit = (typeof MEASUREMENT_UNITS)[number];

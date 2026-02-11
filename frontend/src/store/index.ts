/**
 * Redux Store Exports.
 *
 * Central export point for all store-related functionality.
 * Import from '@store' or '@/store' in components.
 */

// Store
export { store } from './store';
export type { RootState, AppDispatch } from './store';

// Typed Hooks
export { useAppDispatch, useAppSelector } from './hooks';

// Product Slice
export {
  fetchProducts,
  fetchProductById,
  createProduct,
  updateProduct,
  deleteProduct,
  addMaterialToProduct,
  removeMaterialFromProduct,
  clearError,
  setSelectedProduct,
  clearSelectedProduct,
} from './slices/productSlice';

// Raw Material Slice
export {
  fetchRawMaterials,
  createRawMaterial,
  updateRawMaterial,
  adjustRawMaterialStock,
  deleteRawMaterial,
  clearRawMaterialError,
} from './slices/rawMaterialSlice';

// Production Slice
export { calculateProductionPlan, clearPlan, clearProductionError } from './slices/productionSlice';

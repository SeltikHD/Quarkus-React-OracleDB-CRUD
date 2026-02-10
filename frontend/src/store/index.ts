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
  clearError,
  setSelectedProduct,
  clearSelectedProduct,
} from './slices/productSlice';

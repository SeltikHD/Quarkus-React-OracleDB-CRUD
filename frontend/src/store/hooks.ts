import { useDispatch, useSelector } from 'react-redux';

import type { RootState, AppDispatch } from './store';

/**
 * Typed Redux hooks for use throughout the application.
 *
 * ALWAYS use these hooks instead of plain `useDispatch` and `useSelector`.
 * This ensures proper TypeScript inference for the store state and dispatch.
 */

/**
 * Typed useDispatch hook.
 * Use this instead of `useDispatch` from react-redux.
 *
 * @example
 * const dispatch = useAppDispatch();
 * dispatch(fetchProducts());
 */
export const useAppDispatch = useDispatch.withTypes<AppDispatch>();

/**
 * Typed useSelector hook.
 * Use this instead of `useSelector` from react-redux.
 *
 * @example
 * const products = useAppSelector((state) => state.products.items);
 */
export const useAppSelector = useSelector.withTypes<RootState>();

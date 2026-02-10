import { configureStore } from '@reduxjs/toolkit';

import productReducer from './slices/productSlice';

/**
 * Redux Store Configuration for Autoflex.
 *
 * This is the central state store following Redux Toolkit best practices.
 *
 * Architecture:
 * - Feature-based slices (products, rawMaterials, etc.)
 * - RTK Query for API caching (can be added later)
 * - Middleware for logging and async operations
 */
export const store = configureStore({
  reducer: {
    products: productReducer,
    // Add more slices here as the application grows:
    // rawMaterials: rawMaterialReducer,
    // production: productionReducer,
    // ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      // Enable serializable check for debugging
      serializableCheck: {
        // Ignore these paths in state for non-serializable values (if needed)
        ignoredActions: [],
        ignoredPaths: [],
      },
      // Enable immutability check for debugging
      immutableCheck: true,
    }),
  // Enable Redux DevTools in development
  devTools: import.meta.env.DEV,
});

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

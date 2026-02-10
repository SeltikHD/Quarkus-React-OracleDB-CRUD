import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

import { productApi } from '@/services/productApi';
import type { IProduct, IProductRequest } from '@/types/product';

import type { PayloadAction } from '@reduxjs/toolkit';

/**
 * Product state interface.
 */
interface IProductState {
  items: IProduct[];
  selectedProduct: IProduct | null;
  loading: boolean;
  error: string | null;
}

/**
 * Initial state for products.
 */
const initialState: IProductState = {
  items: [],
  selectedProduct: null,
  loading: false,
  error: null,
};

// ============================================================================
// ASYNC THUNKS
// ============================================================================

/**
 * Fetch all products from the API.
 */
export const fetchProducts = createAsyncThunk<
  IProduct[],
  { includeInactive?: boolean } | undefined,
  { rejectValue: string }
>('products/fetchAll', async (params, { rejectWithValue }) => {
  try {
    return await productApi.getAll(params?.includeInactive);
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to fetch products';
    return rejectWithValue(message);
  }
});

/**
 * Fetch a single product by ID.
 */
export const fetchProductById = createAsyncThunk<IProduct, number, { rejectValue: string }>(
  'products/fetchById',
  async (id, { rejectWithValue }) => {
    try {
      return await productApi.getById(id);
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to fetch product';
      return rejectWithValue(message);
    }
  }
);

/**
 * Create a new product.
 */
export const createProduct = createAsyncThunk<IProduct, IProductRequest, { rejectValue: string }>(
  'products/create',
  async (product, { rejectWithValue }) => {
    try {
      return await productApi.create(product);
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to create product';
      return rejectWithValue(message);
    }
  }
);

/**
 * Update an existing product.
 */
export const updateProduct = createAsyncThunk<
  IProduct,
  { id: number; product: IProductRequest },
  { rejectValue: string }
>('products/update', async ({ id, product }, { rejectWithValue }) => {
  try {
    return await productApi.update(id, product);
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to update product';
    return rejectWithValue(message);
  }
});

/**
 * Delete a product.
 */
export const deleteProduct = createAsyncThunk<number, number, { rejectValue: string }>(
  'products/delete',
  async (id, { rejectWithValue }) => {
    try {
      await productApi.delete(id);
      return id;
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to delete product';
      return rejectWithValue(message);
    }
  }
);

// ============================================================================
// SLICE
// ============================================================================

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    /**
     * Clear any error state.
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Set the selected product for viewing/editing.
     */
    setSelectedProduct: (state, action: PayloadAction<IProduct | null>) => {
      state.selectedProduct = action.payload;
    },

    /**
     * Clear the selected product.
     */
    clearSelectedProduct: (state) => {
      state.selectedProduct = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch All Products
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      })

      // Fetch Single Product
      .addCase(fetchProductById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductById.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedProduct = action.payload;
      })
      .addCase(fetchProductById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      })

      // Create Product
      .addCase(createProduct.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createProduct.fulfilled, (state, action) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(createProduct.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      })

      // Update Product
      .addCase(updateProduct.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.items.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
        if (state.selectedProduct?.id === action.payload.id) {
          state.selectedProduct = action.payload;
        }
      })

      // Delete Product
      .addCase(deleteProduct.fulfilled, (state, action) => {
        state.loading = false;
        state.items = state.items.filter((p) => p.id !== action.payload);
        if (state.selectedProduct?.id === action.payload) {
          state.selectedProduct = null;
        }
      });
  },
});

export const { clearError, setSelectedProduct, clearSelectedProduct } = productSlice.actions;

export default productSlice.reducer;

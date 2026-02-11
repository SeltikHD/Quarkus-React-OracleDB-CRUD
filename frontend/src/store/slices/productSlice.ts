import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

import { productApi } from '@/services/productApi';

import type { IBillOfMaterialItemRequest, IProduct, IProductRequest } from '@/types';
import type { PayloadAction } from '@reduxjs/toolkit';

// ============================================================================
// STATE
// ============================================================================

export interface IProductState {
  items: IProduct[];
  selectedProduct: IProduct | null;
  loading: boolean;
  error: string | null;
}

const initialState: IProductState = {
  items: [],
  selectedProduct: null,
  loading: false,
  error: null,
};

// ============================================================================
// ASYNC THUNKS
// ============================================================================

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

export const addMaterialToProduct = createAsyncThunk<
  IProduct,
  { productId: number; request: IBillOfMaterialItemRequest },
  { rejectValue: string }
>('products/addMaterial', async ({ productId, request }, { rejectWithValue }) => {
  try {
    return await productApi.addMaterial(productId, request);
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to add material';
    return rejectWithValue(message);
  }
});

export const removeMaterialFromProduct = createAsyncThunk<
  { productId: number; rawMaterialId: number },
  { productId: number; rawMaterialId: number },
  { rejectValue: string }
>('products/removeMaterial', async ({ productId, rawMaterialId }, { rejectWithValue }) => {
  try {
    await productApi.removeMaterial(productId, rawMaterialId);
    return { productId, rawMaterialId };
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to remove material';
    return rejectWithValue(message);
  }
});

// ============================================================================
// SLICE
// ============================================================================

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setSelectedProduct: (state, action: PayloadAction<IProduct | null>) => {
      state.selectedProduct = action.payload;
    },
    clearSelectedProduct: (state) => {
      state.selectedProduct = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch All
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
      // Fetch By Id
      .addCase(fetchProductById.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedProduct = action.payload;
      })
      // Create
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
      // Update
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
      // Delete
      .addCase(deleteProduct.fulfilled, (state, action) => {
        state.loading = false;
        state.items = state.items.filter((p) => p.id !== action.payload);
        if (state.selectedProduct?.id === action.payload) {
          state.selectedProduct = null;
        }
      })
      // Add Material
      .addCase(addMaterialToProduct.pending, (state) => {
        state.error = null;
      })
      .addCase(addMaterialToProduct.fulfilled, (state, action) => {
        const index = state.items.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
        if (state.selectedProduct?.id === action.payload.id) {
          state.selectedProduct = action.payload;
        }
      })
      .addCase(addMaterialToProduct.rejected, (state, action) => {
        state.error = action.payload ?? 'Unknown error';
      })
      // Remove Material
      .addCase(removeMaterialFromProduct.fulfilled, (state, action) => {
        const { productId, rawMaterialId } = action.payload;
        const product = state.items.find((p) => p.id === productId);
        if (product) {
          product.materials = product.materials.filter((m) => m.rawMaterialId !== rawMaterialId);
        }
        if (state.selectedProduct?.id === productId) {
          state.selectedProduct.materials = state.selectedProduct.materials.filter(
            (m) => m.rawMaterialId !== rawMaterialId
          );
        }
      })
      .addCase(removeMaterialFromProduct.rejected, (state, action) => {
        state.error = action.payload ?? 'Unknown error';
      });
  },
});

export const { clearError, setSelectedProduct, clearSelectedProduct } = productSlice.actions;
export default productSlice.reducer;

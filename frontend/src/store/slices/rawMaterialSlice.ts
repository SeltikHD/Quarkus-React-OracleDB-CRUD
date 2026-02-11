import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

import { rawMaterialApi } from '@/services/rawMaterialApi';

import type { IRawMaterial, IRawMaterialRequest } from '@/types';

// ============================================================================
// STATE
// ============================================================================

export interface IRawMaterialState {
  items: IRawMaterial[];
  loading: boolean;
  error: string | null;
}

const initialState: IRawMaterialState = {
  items: [],
  loading: false,
  error: null,
};

// ============================================================================
// ASYNC THUNKS
// ============================================================================

export const fetchRawMaterials = createAsyncThunk<
  IRawMaterial[],
  { includeInactive?: boolean } | undefined,
  { rejectValue: string }
>('rawMaterials/fetchAll', async (params, { rejectWithValue }) => {
  try {
    return await rawMaterialApi.getAll(params?.includeInactive);
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to fetch raw materials';
    return rejectWithValue(message);
  }
});

export const createRawMaterial = createAsyncThunk<
  IRawMaterial,
  IRawMaterialRequest,
  { rejectValue: string }
>('rawMaterials/create', async (material, { rejectWithValue }) => {
  try {
    return await rawMaterialApi.create(material);
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to create raw material';
    return rejectWithValue(message);
  }
});

export const updateRawMaterial = createAsyncThunk<
  IRawMaterial,
  { id: number; material: IRawMaterialRequest },
  { rejectValue: string }
>('rawMaterials/update', async ({ id, material }, { rejectWithValue }) => {
  try {
    return await rawMaterialApi.update(id, material);
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to update raw material';
    return rejectWithValue(message);
  }
});

export const adjustRawMaterialStock = createAsyncThunk<
  IRawMaterial,
  { id: number; quantity: number },
  { rejectValue: string }
>('rawMaterials/adjustStock', async ({ id, quantity }, { rejectWithValue }) => {
  try {
    return await rawMaterialApi.adjustStock(id, { quantity });
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to adjust stock';
    return rejectWithValue(message);
  }
});

export const deleteRawMaterial = createAsyncThunk<number, number, { rejectValue: string }>(
  'rawMaterials/delete',
  async (id, { rejectWithValue }) => {
    try {
      await rawMaterialApi.delete(id);
      return id;
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to delete raw material';
      return rejectWithValue(message);
    }
  }
);

// ============================================================================
// SLICE
// ============================================================================

const rawMaterialSlice = createSlice({
  name: 'rawMaterials',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch All
      .addCase(fetchRawMaterials.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRawMaterials.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchRawMaterials.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      })
      // Create
      .addCase(createRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createRawMaterial.fulfilled, (state, action) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(createRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      })
      // Update
      .addCase(updateRawMaterial.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateRawMaterial.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.items.findIndex((m) => m.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(updateRawMaterial.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      })
      // Adjust Stock
      .addCase(adjustRawMaterialStock.fulfilled, (state, action) => {
        const index = state.items.findIndex((m) => m.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(adjustRawMaterialStock.rejected, (state, action) => {
        state.error = action.payload ?? 'Unknown error';
      })
      // Delete
      .addCase(deleteRawMaterial.fulfilled, (state, action) => {
        state.items = state.items.filter((m) => m.id !== action.payload);
      })
      .addCase(deleteRawMaterial.rejected, (state, action) => {
        state.error = action.payload ?? 'Unknown error';
      });
  },
});

export const { clearError: clearRawMaterialError } = rawMaterialSlice.actions;
export default rawMaterialSlice.reducer;

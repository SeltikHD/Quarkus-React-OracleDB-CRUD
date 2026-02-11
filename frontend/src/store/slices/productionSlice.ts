import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

import { productionApi } from '@/services/productionApi';

import type { IProductionPlan } from '@/types';

// ============================================================================
// STATE
// ============================================================================

export interface IProductionState {
  plan: IProductionPlan | null;
  loading: boolean;
  error: string | null;
}

const initialState: IProductionState = {
  plan: null,
  loading: false,
  error: null,
};

// ============================================================================
// ASYNC THUNKS
// ============================================================================

export const calculateProductionPlan = createAsyncThunk<
  IProductionPlan,
  void,
  { rejectValue: string }
>('production/calculate', async (_, { rejectWithValue }) => {
  try {
    return await productionApi.calculatePlan();
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Failed to calculate production plan';
    return rejectWithValue(message);
  }
});

// ============================================================================
// SLICE
// ============================================================================

const productionSlice = createSlice({
  name: 'production',
  initialState,
  reducers: {
    clearPlan: (state) => {
      state.plan = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(calculateProductionPlan.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(calculateProductionPlan.fulfilled, (state, action) => {
        state.loading = false;
        state.plan = action.payload;
      })
      .addCase(calculateProductionPlan.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload ?? 'Unknown error';
      });
  },
});

export const { clearPlan, clearError: clearProductionError } = productionSlice.actions;
export default productionSlice.reducer;

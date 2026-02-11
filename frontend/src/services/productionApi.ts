import apiClient from './apiClient';

import type { IProductionPlan } from '@/types';

/**
 * Production API service.
 *
 * Provides methods for production planning operations.
 */
export const productionApi = {
  /**
   * Calculate the optimal production plan using the greedy algorithm.
   *
   * This endpoint evaluates all active products with BOM definitions
   * against current raw material stock levels and determines the maximum
   * number of units that can be produced, prioritizing by unit price.
   */
  async calculatePlan(): Promise<IProductionPlan> {
    const response = await apiClient.post<IProductionPlan>('/production/calculate');
    return response.data;
  },
};

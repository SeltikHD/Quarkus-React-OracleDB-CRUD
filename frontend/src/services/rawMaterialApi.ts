import apiClient from './apiClient';

import type { IRawMaterial, IRawMaterialRequest, IStockAdjustmentRequest } from '@/types';

/**
 * Raw Material API service.
 *
 * Provides methods for interacting with the raw-materials endpoint.
 */
export const rawMaterialApi = {
  /**
   * Fetch all raw materials.
   */
  async getAll(includeInactive = false): Promise<IRawMaterial[]> {
    const params = new URLSearchParams();
    if (includeInactive) {
      params.append('includeInactive', 'true');
    }

    const response = await apiClient.get<IRawMaterial[]>(`/raw-materials?${params.toString()}`);
    return response.data;
  },

  /**
   * Fetch a single raw material by ID.
   */
  async getById(id: number): Promise<IRawMaterial> {
    const response = await apiClient.get<IRawMaterial>(`/raw-materials/${String(id)}`);
    return response.data;
  },

  /**
   * Create a new raw material.
   */
  async create(material: IRawMaterialRequest): Promise<IRawMaterial> {
    const response = await apiClient.post<IRawMaterial>('/raw-materials', material);
    return response.data;
  },

  /**
   * Update an existing raw material.
   */
  async update(id: number, material: IRawMaterialRequest): Promise<IRawMaterial> {
    const response = await apiClient.put<IRawMaterial>(`/raw-materials/${String(id)}`, material);
    return response.data;
  },

  /**
   * Delete or deactivate a raw material.
   */
  async delete(id: number, permanent = false): Promise<void> {
    const params = permanent ? '?permanent=true' : '';
    await apiClient.delete(`/raw-materials/${String(id)}${params}`);
  },

  /**
   * Adjust stock quantity for a raw material.
   */
  async adjustStock(id: number, request: IStockAdjustmentRequest): Promise<IRawMaterial> {
    const response = await apiClient.patch<IRawMaterial>(
      `/raw-materials/${String(id)}/stock`,
      request
    );
    return response.data;
  },
};

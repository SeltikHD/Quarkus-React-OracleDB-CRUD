import apiClient from './apiClient';

import type {
  IBillOfMaterialItem,
  IBillOfMaterialItemRequest,
  IProduct,
  IProductRequest,
  IUpdateMaterialQuantityRequest,
} from '@/types';

/**
 * Product API service.
 *
 * Provides methods for interacting with the products endpoint,
 * including bill-of-materials management.
 */
export const productApi = {
  /**
   * Fetch all products.
   */
  async getAll(includeInactive = false): Promise<IProduct[]> {
    const params = new URLSearchParams();
    if (includeInactive) {
      params.append('includeInactive', 'true');
    }

    const response = await apiClient.get<IProduct[]>(`/products?${params.toString()}`);
    return response.data;
  },

  /**
   * Fetch a single product by ID.
   */
  async getById(id: number): Promise<IProduct> {
    const response = await apiClient.get<IProduct>(`/products/${String(id)}`);
    return response.data;
  },

  /**
   * Search products by name.
   */
  async search(term: string): Promise<IProduct[]> {
    const response = await apiClient.get<IProduct[]>(
      `/products?search=${encodeURIComponent(term)}`
    );
    return response.data;
  },

  /**
   * Create a new product.
   */
  async create(product: IProductRequest): Promise<IProduct> {
    const response = await apiClient.post<IProduct>('/products', product);
    return response.data;
  },

  /**
   * Update an existing product.
   */
  async update(id: number, product: IProductRequest): Promise<IProduct> {
    const response = await apiClient.put<IProduct>(`/products/${String(id)}`, product);
    return response.data;
  },

  /**
   * Delete a product (soft delete by default).
   */
  async delete(id: number, permanent = false): Promise<void> {
    const params = permanent ? '?permanent=true' : '';
    await apiClient.delete(`/products/${String(id)}${params}`);
  },

  /**
   * Adjust product stock.
   */
  async adjustStock(id: number, delta: number): Promise<IProduct> {
    const response = await apiClient.patch<IProduct>(
      `/products/${String(id)}/stock?delta=${String(delta)}`
    );
    return response.data;
  },

  // ==========================================================================
  // BILL OF MATERIALS
  // ==========================================================================

  /**
   * Get the bill of materials for a product.
   */
  async getMaterials(productId: number): Promise<IBillOfMaterialItem[]> {
    const response = await apiClient.get<IBillOfMaterialItem[]>(
      `/products/${String(productId)}/materials`
    );
    return response.data;
  },

  /**
   * Add a material to a product's BOM.
   */
  async addMaterial(productId: number, request: IBillOfMaterialItemRequest): Promise<IProduct> {
    const response = await apiClient.post<IProduct>(
      `/products/${String(productId)}/materials`,
      request
    );
    return response.data;
  },

  /**
   * Update a material quantity in a product's BOM.
   */
  async updateMaterialQuantity(
    productId: number,
    rawMaterialId: number,
    request: IUpdateMaterialQuantityRequest
  ): Promise<IProduct> {
    const response = await apiClient.put<IProduct>(
      `/products/${String(productId)}/materials/${String(rawMaterialId)}`,
      request
    );
    return response.data;
  },

  /**
   * Remove a material from a product's BOM.
   */
  async removeMaterial(productId: number, rawMaterialId: number): Promise<void> {
    await apiClient.delete(`/products/${String(productId)}/materials/${String(rawMaterialId)}`);
  },
};

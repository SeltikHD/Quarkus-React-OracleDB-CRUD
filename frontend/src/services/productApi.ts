import axios from 'axios';

import type { IProduct, IProductRequest } from '@/types/product';

/**
 * Axios instance configured for the Autoflex API.
 */
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request interceptor for adding auth headers, logging, etc.
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token if available
    // const token = localStorage.getItem('authToken');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error: Error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error)) {
      // Handle specific error cases
      if (error.response?.status === 401) {
        // Handle unauthorized - redirect to login, clear tokens, etc.
        // window.location.href = '/login';
      }

      // Extract error message from response
      const message =
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        (error.response?.data?.message as string | undefined) ?? 'An unexpected error occurred';

      return Promise.reject(new Error(message));
    }

    const errorMessage = error instanceof Error ? error.message : 'An unexpected error occurred';
    return Promise.reject(new Error(errorMessage));
  }
);

/**
 * Product API service.
 *
 * Provides methods for interacting with the products endpoint.
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
    const response = await apiClient.get<IProduct>(`/products/${id}`);
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
    const response = await apiClient.put<IProduct>(`/products/${id}`, product);
    return response.data;
  },

  /**
   * Delete a product (soft delete by default).
   */
  async delete(id: number, permanent = false): Promise<void> {
    const params = permanent ? '?permanent=true' : '';
    await apiClient.delete(`/products/${id}${params}`);
  },

  /**
   * Adjust product stock.
   */
  async adjustStock(id: number, delta: number): Promise<IProduct> {
    const response = await apiClient.patch<IProduct>(`/products/${id}/stock?delta=${delta}`);
    return response.data;
  },
};

export default apiClient;

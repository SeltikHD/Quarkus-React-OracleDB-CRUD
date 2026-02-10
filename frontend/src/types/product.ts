/**
 * Product type definitions.
 *
 * These interfaces define the shape of product data throughout the application.
 * They mirror the backend DTOs for type safety.
 */

/**
 * Product entity returned from the API.
 */
export interface IProduct {
  id: number;
  name: string;
  description: string | null;
  sku: string;
  unitPrice: number;
  stockQuantity: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request body for creating or updating a product.
 */
export interface IProductRequest {
  name: string;
  description?: string;
  sku: string;
  unitPrice: number;
  stockQuantity?: number;
}

/**
 * API error response structure.
 */
export interface IApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}

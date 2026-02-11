import axios from 'axios';

/**
 * Shared Axios instance configured for the Autoflex API.
 *
 * All API services import this client to ensure consistent
 * configuration, interceptors, and error handling.
 */
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

// Response interceptor for consistent error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error)) {
      const message =
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        (error.response?.data?.message as string | undefined) ?? error.message;

      return Promise.reject(new Error(message));
    }

    const errorMessage = error instanceof Error ? error.message : 'An unexpected error occurred';
    return Promise.reject(new Error(errorMessage));
  }
);

export default apiClient;

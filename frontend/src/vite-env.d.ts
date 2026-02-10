/// <reference types="vite/client" />

/**
 * Vite environment variable type definitions.
 *
 * Declare all VITE_ prefixed environment variables here
 * for TypeScript autocompletion and type safety.
 */
interface ImportMetaEnv {
  /** API base URL (defaults to /api/v1 in development) */
  readonly VITE_API_BASE_URL?: string;

  /** Application environment */
  readonly MODE: 'development' | 'production' | 'test';

  /** Whether running in development mode */
  readonly DEV: boolean;

  /** Whether running in production mode */
  readonly PROD: boolean;

  /** Whether running in SSR mode */
  readonly SSR: boolean;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

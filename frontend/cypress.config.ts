import { defineConfig } from 'cypress';

/**
 * Cypress E2E Testing Configuration for Autoflex Frontend.
 *
 * This configuration sets up end-to-end testing with best practices
 * for a React + Vite application.
 */
export default defineConfig({
  e2e: {
    // Base URL for the application under test
    baseUrl: 'http://localhost:5173',

    // Test file patterns
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'cypress/support/e2e.ts',

    // Viewport settings (Desktop-first)
    viewportWidth: 1280,
    viewportHeight: 720,

    // Video and screenshot settings
    video: false, // Disable video recording for faster CI runs
    screenshotOnRunFailure: true,
    screenshotsFolder: 'cypress/screenshots',
    videosFolder: 'cypress/videos',

    // Timeouts
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 30000,
    pageLoadTimeout: 60000,

    // Retry configuration for flaky tests
    retries: {
      runMode: 2, // Retries in CI
      openMode: 0, // No retries in interactive mode
    },

    // Experimental features
    experimentalRunAllSpecs: true,

    setupNodeEvents(on, config) {
      // Implement node event listeners here if needed
      // For example: code coverage, custom logging, etc.

      return config;
    },
  },

  // Component testing configuration (optional, for future use)
  component: {
    devServer: {
      framework: 'react',
      bundler: 'vite',
    },
    specPattern: 'src/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'cypress/support/component.ts',
  },

  // Environment variables
  env: {
    // API base URL for intercepting requests
    apiUrl: 'http://localhost:8080/api/v1',
  },
});

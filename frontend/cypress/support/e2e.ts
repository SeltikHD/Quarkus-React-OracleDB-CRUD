/**
 * Cypress E2E Support File.
 *
 * This file is loaded before each E2E test file.
 * Use it to set up global configuration, custom commands, and hooks.
 */

// Import custom commands
import './commands';

// Prevent TypeScript errors when using cy.intercept
declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      /**
       * Custom command to intercept and mock API calls.
       * @example cy.mockApi('GET', '/products', [{ id: 1, name: 'Product 1' }])
       */
      mockApi(method: string, url: string, response: unknown): Chainable<void>;
    }
  }
}

// Hide fetch/XHR requests from command log for cleaner output
const app = window.top;
if (app !== null && !app.document.head.querySelector('[data-hide-command-log-request]')) {
  const style = app.document.createElement('style');
  style.innerHTML = '.command-name-request, .command-name-xhr { display: none }';
  style.setAttribute('data-hide-command-log-request', '');
  app.document.head.appendChild(style);
}

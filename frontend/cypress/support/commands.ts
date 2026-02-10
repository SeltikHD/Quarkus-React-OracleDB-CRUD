/**
 * Cypress Custom Commands.
 *
 * Add custom commands here for reusable test actions.
 * These commands are available globally in all test files.
 */

/**
 * Mock API responses for testing.
 *
 * @example
 * cy.mockApi('GET', '/products', [{ id: 1, name: 'Test Product' }]);
 */
Cypress.Commands.add('mockApi', (method: string, url: string, response: unknown) => {
  const apiUrl = Cypress.env('apiUrl') as string;
  cy.intercept(method, `${apiUrl}${url}`, {
    statusCode: 200,
    body: response,
  }).as(`mock${method}${url.replace(/\//g, '_')}`);
});

/**
 * Get an element by data-testid attribute.
 * Encourages using data-testid for test selectors instead of CSS classes.
 *
 * @example
 * cy.getByTestId('submit-button').click();
 */
Cypress.Commands.add('getByTestId', (testId: string) => {
  return cy.get(`[data-testid="${testId}"]`);
});

// Extend Chainable interface for TypeScript
declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      /**
       * Get element by data-testid attribute.
       * @example cy.getByTestId('product-list')
       */
      getByTestId(testId: string): Chainable<JQuery<HTMLElement>>;
    }
  }
}

export {};

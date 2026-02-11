/**
 * Cypress Custom Commands for Autoflex ERP E2E Tests.
 *
 * Provides reusable commands for API mocking, element selection,
 * and common user interactions.
 */

/**
 * Mock all API responses for a given page context.
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
 */
Cypress.Commands.add('getByTestId', (testId: string) => {
  return cy.get(`[data-testid="${testId}"]`);
});

/**
 * Mock the full raw materials API (GET list).
 */
Cypress.Commands.add('mockRawMaterials', () => {
  cy.fixture('raw-materials.json').then((materials: unknown) => {
    cy.intercept('GET', '**/api/v1/raw-materials*', {
      statusCode: 200,
      body: materials,
    }).as('getRawMaterials');
  });
});

/**
 * Mock the full products API (GET list).
 */
Cypress.Commands.add('mockProducts', () => {
  cy.fixture('products.json').then((products: unknown) => {
    cy.intercept('GET', '**/api/v1/products*', {
      statusCode: 200,
      body: products,
    }).as('getProducts');
  });
});

/**
 * Mock the production calculation API.
 */
Cypress.Commands.add('mockProduction', () => {
  cy.fixture('production-plan.json').then((plan: unknown) => {
    cy.intercept('POST', '**/api/v1/production/calculate', {
      statusCode: 200,
      body: plan,
    }).as('calculateProduction');
  });
});

/**
 * Fill a MUI TextField by its label text.
 */
Cypress.Commands.add('fillField', (label: string, value: string) => {
  cy.contains('label', label).parent().find('input, textarea').first().clear().type(value);
});

/**
 * Select a value from a MUI Select dropdown by its label and option text.
 */
Cypress.Commands.add('selectOption', (label: string, optionText: string) => {
  cy.contains('label', label).parent().find('[role="combobox"]').click();
  cy.get('[role="listbox"]').contains(optionText).click();
});

// Extend Chainable interface for TypeScript
declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      getByTestId(testId: string): Chainable<JQuery<HTMLElement>>;
      mockApi(method: string, url: string, response: unknown): Chainable<void>;
      mockRawMaterials(): Chainable<void>;
      mockProducts(): Chainable<void>;
      mockProduction(): Chainable<void>;
      fillField(label: string, value: string): Chainable<void>;
      selectOption(label: string, optionText: string): Chainable<void>;
    }
  }
}

export {};

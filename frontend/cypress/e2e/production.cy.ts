/**
 * Production Planning Page E2E Tests.
 *
 * Tests production plan calculation, result display, and edge cases.
 */

describe('Production Planning', () => {
  beforeEach(() => {
    cy.visit('/production');
  });

  describe('Empty State', () => {
    it('should show the empty state before calculation', () => {
      cy.contains('Ready to Plan Production').should('be.visible');
      cy.contains('Greedy Algorithm').should('be.visible');
    });

    it('should display the calculate button', () => {
      cy.contains('button', 'Calculate Production Plan').should('be.visible');
    });

    it('should show production planning heading', () => {
      cy.contains('Production Planning').should('be.visible');
    });
  });

  describe('Calculate Production Plan', () => {
    it('should calculate and display production results', () => {
      cy.mockProduction();

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@calculateProduction');

      // Summary cards
      cy.contains('Total Production Value').should('be.visible');
      cy.contains('$1,199.60').should('be.visible');

      cy.contains('Total Units to Produce').should('be.visible');
      cy.contains('40').should('be.visible');

      cy.contains('Product Lines').should('be.visible');
      cy.contains('1').should('be.visible');
    });

    it('should render the plan table with product data', () => {
      cy.mockProduction();

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@calculateProduction');

      cy.contains('Production Plan').should('be.visible');
      cy.contains('Electronic Component XYZ').should('be.visible');
      cy.contains('COMP-XYZ-001').should('be.visible');
    });

    it('should show remaining stock chips', () => {
      cy.mockProduction();

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@calculateProduction');

      cy.contains('Remaining Raw Material Stock').should('be.visible');
      cy.contains('Carbon Steel Rod: 400').should('be.visible');
      cy.contains('Copper Wire: 960').should('be.visible');
    });

    it('should display the TOTAL row', () => {
      cy.mockProduction();

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@calculateProduction');

      cy.contains('TOTAL').should('be.visible');
    });
  });

  describe('Clear Plan', () => {
    it('should show the Clear button after calculation', () => {
      cy.mockProduction();

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@calculateProduction');

      cy.contains('button', 'Clear').should('be.visible');
    });

    it('should return to empty state after clearing', () => {
      cy.mockProduction();

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@calculateProduction');

      cy.contains('button', 'Clear').click();
      cy.contains('Ready to Plan Production').should('be.visible');
    });
  });

  describe('Empty Production Plan', () => {
    it('should show a warning when no products can be produced', () => {
      cy.intercept('POST', '**/api/v1/production/calculate', {
        statusCode: 200,
        body: {
          items: [],
          totalProductionValue: 0,
          totalUnits: 0,
          remainingStock: {},
        },
      }).as('emptyPlan');

      cy.contains('button', 'Calculate Production Plan').click();
      cy.wait('@emptyPlan');

      cy.contains('No products can be produced').should('be.visible');
    });
  });
});

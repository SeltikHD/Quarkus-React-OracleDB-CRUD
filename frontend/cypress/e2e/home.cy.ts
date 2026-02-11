/**
 * Dashboard Page E2E Tests.
 *
 * Verifies that the home/dashboard page loads correctly
 * and navigation to sub-pages works.
 */

describe('Dashboard Page', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should display the application title', () => {
    cy.contains('Autoflex ERP').should('be.visible');
  });

  it('should display the application description', () => {
    cy.contains('Product and Raw Material Management System').should('be.visible');
  });

  it('should display three navigation cards', () => {
    cy.contains('Raw Materials').should('be.visible');
    cy.contains('Products').should('be.visible');
    cy.contains('Production Planning').should('be.visible');
  });

  it('should navigate to raw materials page', () => {
    cy.mockRawMaterials();
    cy.contains('Raw Materials').click();
    cy.url().should('include', '/raw-materials');
  });

  it('should navigate to products page', () => {
    cy.mockProducts();
    cy.mockRawMaterials();
    cy.contains('Products').click();
    cy.url().should('include', '/products');
  });

  it('should navigate to production page', () => {
    cy.contains('Production Planning').click();
    cy.url().should('include', '/production');
  });

  it('should have sidebar visible with navigation links', () => {
    cy.get('nav').should('be.visible');
  });
});

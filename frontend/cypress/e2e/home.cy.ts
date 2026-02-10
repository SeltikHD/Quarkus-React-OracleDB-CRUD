/**
 * Home Page E2E Tests.
 *
 * These tests verify that the home page loads correctly
 * and displays the expected content.
 */

describe('Home Page', () => {
  beforeEach(() => {
    // Visit the home page before each test
    cy.visit('/');
  });

  it('should display the application title', () => {
    cy.contains('h1', 'Welcome to Autoflex ERP').should('be.visible');
  });

  it('should display the application description', () => {
    cy.contains('Product and Raw Material Management System').should('be.visible');
  });

  it('should have the correct page title', () => {
    cy.title().should('eq', 'Autoflex ERP');
  });

  it('should be responsive on mobile viewport', () => {
    cy.viewport('iphone-x');
    cy.contains('h1', 'Welcome to Autoflex ERP').should('be.visible');
  });

  it('should be responsive on tablet viewport', () => {
    cy.viewport('ipad-2');
    cy.contains('h1', 'Welcome to Autoflex ERP').should('be.visible');
  });
});

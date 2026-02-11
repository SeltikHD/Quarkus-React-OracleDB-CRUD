/**
 * Raw Materials Page E2E Tests.
 *
 * Tests the complete CRUD flow for raw materials:
 * List, Create, Edit, Adjust Stock, Delete.
 */

describe('Raw Materials Management', () => {
  beforeEach(() => {
    cy.mockRawMaterials();
    cy.visit('/raw-materials');
    cy.wait('@getRawMaterials');
  });

  describe('Listing', () => {
    it('should display the raw materials table', () => {
      cy.getByTestId('raw-materials-table').should('be.visible');
    });

    it('should display all raw materials from the API', () => {
      cy.contains('Carbon Steel Rod').should('be.visible');
      cy.contains('Copper Wire').should('be.visible');
      cy.contains('Plastic Resin').should('be.visible');
    });

    it('should show material codes as chips', () => {
      cy.contains('RM-STEEL-001').should('be.visible');
      cy.contains('RM-COPPER-002').should('be.visible');
    });

    it('should show Active/Inactive status', () => {
      cy.contains('Active').should('be.visible');
      cy.contains('Inactive').should('be.visible');
    });

    it('should display stock quantities', () => {
      cy.getByTestId('material-row-1').within(() => {
        cy.contains('500').should('be.visible');
      });
    });
  });

  describe('Create', () => {
    it('should open the add material modal', () => {
      cy.getByTestId('add-material-btn').click();
      cy.contains('Add Raw Material').should('be.visible');
    });

    it('should create a new raw material', () => {
      const newMaterial = {
        id: 4,
        name: 'Aluminum Sheet',
        description: 'Lightweight aluminum sheets',
        code: 'RM-ALUM-004',
        unit: 'UNIT',
        unitAbbreviation: 'un',
        stockQuantity: 100,
        unitCost: 5.00,
        active: true,
        createdAt: '2026-02-01T10:00:00',
        updatedAt: '2026-02-01T10:00:00',
      };

      cy.intercept('POST', '**/api/v1/raw-materials', {
        statusCode: 200,
        body: newMaterial,
      }).as('createMaterial');

      cy.getByTestId('add-material-btn').click();
      cy.fillField('Name', 'Aluminum Sheet');
      cy.fillField('Code', 'RM-ALUM-004');
      cy.fillField('Description', 'Lightweight aluminum sheets');
      cy.fillField('Unit Cost', '5');
      cy.fillField('Initial Stock Quantity', '100');

      cy.contains('button', 'Create').click();
      cy.wait('@createMaterial');

      // Success notification should appear
      cy.contains('Raw material created successfully').should('be.visible');
    });

    it('should disable Create button when form is invalid', () => {
      cy.getByTestId('add-material-btn').click();
      // Empty form — Create button should be disabled
      cy.contains('button', 'Create').should('be.disabled');
    });

    it('should close modal on Cancel', () => {
      cy.getByTestId('add-material-btn').click();
      cy.contains('Add Raw Material').should('be.visible');
      cy.contains('button', 'Cancel').click();
      cy.contains('Add Raw Material').should('not.exist');
    });
  });

  describe('Edit', () => {
    it('should open edit modal with pre-filled data', () => {
      cy.getByTestId('material-row-1').within(() => {
        cy.getByTestId('edit-material-btn').click();
      });
      cy.contains('Edit Raw Material').should('be.visible');
      // Verify the form is pre-populated
      cy.get('input[value="Carbon Steel Rod"]').should('exist');
    });

    it('should save changes to a raw material', () => {
      const updatedMaterial = {
        id: 1,
        name: 'Premium Steel Rod',
        description: 'High-grade carbon steel for structural use',
        code: 'RM-STEEL-001',
        unit: 'KILOGRAM',
        unitAbbreviation: 'kg',
        stockQuantity: 500,
        unitCost: 4.00,
        active: true,
        createdAt: '2026-01-10T08:00:00',
        updatedAt: '2026-02-01T12:00:00',
      };

      cy.intercept('PUT', '**/api/v1/raw-materials/1', {
        statusCode: 200,
        body: updatedMaterial,
      }).as('updateMaterial');

      cy.getByTestId('material-row-1').within(() => {
        cy.getByTestId('edit-material-btn').click();
      });

      cy.fillField('Name', 'Premium Steel Rod');
      cy.fillField('Unit Cost', '4');

      cy.contains('button', 'Save Changes').click();
      cy.wait('@updateMaterial');
      cy.contains('Raw material updated successfully').should('be.visible');
    });
  });

  describe('Adjust Stock', () => {
    it('should open stock adjustment dialog', () => {
      cy.getByTestId('material-row-1').within(() => {
        cy.getByTestId('adjust-stock-btn').click();
      });
      cy.contains('Adjust Stock').should('be.visible');
      cy.contains('Carbon Steel Rod').should('be.visible');
      cy.contains('Current Stock').should('be.visible');
    });

    it('should adjust stock quantity', () => {
      const updatedMaterial = {
        id: 1,
        name: 'Carbon Steel Rod',
        description: 'High-grade carbon steel for structural use',
        code: 'RM-STEEL-001',
        unit: 'KILOGRAM',
        unitAbbreviation: 'kg',
        stockQuantity: 600,
        unitCost: 3.50,
        active: true,
        createdAt: '2026-01-10T08:00:00',
        updatedAt: '2026-02-01T12:00:00',
      };

      cy.intercept('PATCH', '**/api/v1/raw-materials/1/stock', {
        statusCode: 200,
        body: updatedMaterial,
      }).as('adjustStock');

      cy.getByTestId('material-row-1').within(() => {
        cy.getByTestId('adjust-stock-btn').click();
      });

      cy.fillField('Quantity to Adjust', '100');
      cy.contains('button', 'Adjust').click();
      cy.wait('@adjustStock');
      cy.contains('Stock adjusted successfully').should('be.visible');
    });
  });

  describe('Delete', () => {
    it('should open delete confirmation dialog', () => {
      cy.getByTestId('material-row-2').within(() => {
        cy.getByTestId('delete-material-btn').click();
      });
      cy.contains('Delete Raw Material').should('be.visible');
      cy.contains('Are you sure').should('be.visible');
    });

    it('should delete a raw material on confirmation', () => {
      cy.intercept('DELETE', '**/api/v1/raw-materials/2', {
        statusCode: 200,
        body: {},
      }).as('deleteMaterial');

      cy.getByTestId('material-row-2').within(() => {
        cy.getByTestId('delete-material-btn').click();
      });

      cy.contains('button', 'Delete').click();
      cy.wait('@deleteMaterial');
      cy.contains('Raw material deleted successfully').should('be.visible');
    });

    it('should show error when deleting a material used in BOM', () => {
      cy.intercept('DELETE', '**/api/v1/raw-materials/1', {
        statusCode: 409,
        body: {
          message: 'Cannot delete raw material: it is used in product bill of materials',
        },
      }).as('deleteUsedMaterial');

      cy.getByTestId('material-row-1').within(() => {
        cy.getByTestId('delete-material-btn').click();
      });

      cy.contains('button', 'Delete').click();
      cy.wait('@deleteUsedMaterial');
      cy.contains('Cannot delete raw material').should('be.visible');
    });

    it('should cancel delete without deleting', () => {
      cy.getByTestId('material-row-1').within(() => {
        cy.getByTestId('delete-material-btn').click();
      });
      cy.contains('button', 'Cancel').click();
      // Dialog should be closed; material still present
      cy.contains('Delete Raw Material').should('not.exist');
      cy.contains('Carbon Steel Rod').should('be.visible');
    });
  });
});

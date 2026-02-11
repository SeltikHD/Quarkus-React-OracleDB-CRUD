/**
 * Products Page E2E Tests.
 *
 * Tests the complete CRUD flow for products:
 * List, Create, Edit, Delete, and BOM management.
 */

describe('Products Management', () => {
  beforeEach(() => {
    cy.mockProducts();
    cy.mockRawMaterials();
    cy.visit('/products');
    cy.wait('@getProducts');
  });

  describe('Listing', () => {
    it('should display the products table', () => {
      cy.getByTestId('products-table').should('be.visible');
    });

    it('should display all products from the API', () => {
      cy.contains('Electronic Component XYZ').should('be.visible');
      cy.contains('Industrial Sensor ABC').should('be.visible');
    });

    it('should show SKU as chips', () => {
      cy.contains('COMP-XYZ-001').should('be.visible');
      cy.contains('SENS-ABC-002').should('be.visible');
    });

    it('should show material count for products with BOM', () => {
      cy.getByTestId('product-row-1').within(() => {
        cy.contains('2 items').should('be.visible');
      });
    });

    it('should show Active status', () => {
      cy.contains('Active').should('be.visible');
    });
  });

  describe('Create', () => {
    it('should open add product modal', () => {
      cy.getByTestId('add-product-btn').click();
      cy.contains('Add Product').should('be.visible');
    });

    it('should create a new product', () => {
      const newProduct = {
        id: 3,
        name: 'Motor Assembly',
        description: 'Electric motor for conveyor systems',
        sku: 'MTR-ASM-003',
        unitPrice: 2500,
        stockQuantity: 10,
        active: true,
        createdAt: '2026-02-01T10:00:00',
        updatedAt: '2026-02-01T10:00:00',
        materials: [],
      };

      cy.intercept('POST', '**/api/v1/products', {
        statusCode: 201,
        body: newProduct,
      }).as('createProduct');

      cy.getByTestId('add-product-btn').click();
      cy.fillField('Name', 'Motor Assembly');
      cy.fillField('SKU', 'MTR-ASM-003');
      cy.fillField('Description', 'Electric motor for conveyor systems');
      cy.fillField('Unit Price', '250');
      cy.fillField('Initial Stock Quantity', '10');

      cy.contains('button', 'Create').click();
      cy.wait('@createProduct');
      cy.contains('Product created successfully').should('be.visible');
    });

    it('should disable Create button when required fields are empty', () => {
      cy.getByTestId('add-product-btn').click();
      cy.contains('button', 'Create').should('be.disabled');
    });

    it('should require name field', () => {
      cy.getByTestId('add-product-btn').click();
      // Fill only SKU and price — name is missing
      cy.fillField('SKU', 'TEST-001');
      cy.fillField('Unit Price', '10');
      cy.contains('button', 'Create').should('be.disabled');
    });
  });

  describe('Edit', () => {
    it('should open edit modal with pre-filled data', () => {
      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('edit-product-btn').click();
      });
      cy.contains('Edit Product').should('be.visible');
      cy.get('input[value="Electronic Component XYZ"]').should('exist');
      cy.get('input[value="COMP-XYZ-001"]').should('exist');
    });

    it('should update a product name', () => {
      const updatedProduct = {
        id: 1,
        name: 'Premium Electronic Component',
        description: 'High-quality electronic component for industrial use',
        sku: 'COMP-XYZ-001',
        unitPrice: 35.99,
        stockQuantity: 100,
        active: true,
        createdAt: '2026-01-15T10:30:00',
        updatedAt: '2026-02-01T12:00:00',
        materials: [
          { rawMaterialId: 1, quantityRequired: 2.5 },
          { rawMaterialId: 2, quantityRequired: 1 },
        ],
      };

      cy.intercept('PUT', '**/api/v1/products/1', {
        statusCode: 200,
        body: updatedProduct,
      }).as('updateProduct');

      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('edit-product-btn').click();
      });

      cy.fillField('Name', 'Premium Electronic Component');
      cy.fillField('Unit Price', '35.99');

      cy.contains('button', 'Save Changes').click();
      cy.wait('@updateProduct');
      cy.contains('Product updated successfully').should('be.visible');
    });
  });

  describe('Delete', () => {
    it('should open delete confirmation dialog', () => {
      cy.getByTestId('product-row-2').within(() => {
        cy.getByTestId('delete-product-btn').click();
      });
      cy.contains('Delete Product').should('be.visible');
      cy.contains('soft-delete').should('be.visible');
    });

    it('should delete a product on confirmation', () => {
      cy.intercept('DELETE', '**/api/v1/products/2', {
        statusCode: 204,
      }).as('deleteProduct');

      cy.getByTestId('product-row-2').within(() => {
        cy.getByTestId('delete-product-btn').click();
      });

      cy.contains('button', 'Delete').click();
      cy.wait('@deleteProduct');
      cy.contains('Product deleted successfully').should('be.visible');
    });

    it('should cancel delete without deleting', () => {
      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('delete-product-btn').click();
      });
      cy.contains('button', 'Cancel').click();
      cy.contains('Delete Product').should('not.exist');
      cy.contains('Electronic Component XYZ').should('be.visible');
    });
  });

  describe('BOM Management', () => {
    it('should open BOM drawer', () => {
      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('manage-bom-btn').click();
      });
      cy.contains('Manage BOM').should('be.visible');
      cy.contains('Electronic Component XYZ (COMP-XYZ-001)').should('be.visible');
    });

    it('should display current materials in BOM', () => {
      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('manage-bom-btn').click();
      });
      cy.getByTestId('bom-drawer').contains('Current Materials (2)').should('be.visible');
    });

    it('should add a material to BOM', () => {
      const updatedProduct = {
        id: 2,
        name: 'Industrial Sensor ABC',
        description: 'Precision sensor for manufacturing processes',
        sku: 'SENS-ABC-002',
        unitPrice: 149.5,
        stockQuantity: 50,
        active: true,
        createdAt: '2026-01-14T09:15:00',
        updatedAt: '2026-02-01T12:00:00',
        materials: [{ rawMaterialId: 1, quantityRequired: 3 }],
      };

      cy.intercept('POST', '**/api/v1/products/2/materials', {
        statusCode: 200,
        body: updatedProduct,
      }).as('addMaterial');

      // Open BOM drawer for product without materials
      cy.getByTestId('product-row-2').within(() => {
        cy.getByTestId('manage-bom-btn').click();
      });

      cy.wait('@getRawMaterials');

      // Select a raw material and set quantity
      cy.getByTestId('bom-drawer').within(() => {
        cy.contains('label', 'Raw Material')
          .parent()
          .find('[role="combobox"]')
          .click();
      });
      cy.get('[role="listbox"]').contains('Carbon Steel Rod').click();
      cy.getByTestId('bom-drawer').within(() => {
        cy.contains('label', 'Qty').parent().find('input').clear().type('3');
      });

      // Click add button
      cy.getByTestId('add-to-bom-btn').click();
      cy.wait('@addMaterial');
      cy.contains('Material added to BOM').should('be.visible');
    });

    it('should remove a material from BOM', () => {
      cy.intercept('DELETE', '**/api/v1/products/1/materials/1', {
        statusCode: 200,
        body: {},
      }).as('removeMaterial');

      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('manage-bom-btn').click();
      });

      cy.wait('@getRawMaterials');

      // Click remove on first material
      cy.getByTestId('bom-drawer').find('[data-testid="remove-material-btn"]').first().click();
      cy.wait('@removeMaterial');
      cy.contains('Material removed from BOM').should('be.visible');
    });

    it('should close BOM drawer', () => {
      cy.getByTestId('product-row-1').within(() => {
        cy.getByTestId('manage-bom-btn').click();
      });
      cy.getByTestId('bom-drawer').contains('button', 'Close').click();
      cy.getByTestId('bom-drawer').should('not.exist');
    });
  });
});

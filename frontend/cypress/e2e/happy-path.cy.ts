/**
 * Happy Path E2E Tests.
 *
 * Full user journey: Create RM → Adjust Stock → Create Product →
 * Add Material to Product → Calculate Production → Verify Results.
 * Plus CRUD check and validation scenarios.
 */

describe('Happy Path — Full User Journey', () => {
  const newMaterial = {
    id: 4,
    name: 'Aluminum Sheet',
    description: 'Thin aluminum sheets for housing',
    code: 'ALU-SHT-004',
    unit: 'KG',
    unitCost: 12.5,
    stockQuantity: 0,
    active: true,
    createdAt: '2026-02-01T10:00:00',
    updatedAt: '2026-02-01T10:00:00',
  };

  const newProduct = {
    id: 3,
    name: 'Aluminum Housing',
    description: 'Protective housing for sensors',
    sku: 'HSG-ALU-003',
    unitPrice: 85.0,
    stockQuantity: 0,
    active: true,
    createdAt: '2026-02-01T11:00:00',
    updatedAt: '2026-02-01T11:00:00',
    materials: [],
  };

  const productWithBom = {
    ...newProduct,
    materials: [{ rawMaterialId: 4, quantityRequired: 2.0 }],
    updatedAt: '2026-02-01T12:00:00',
  };

  const productionPlan = {
    items: [
      {
        productId: 3,
        productName: 'Aluminum Housing',
        productSku: 'HSG-ALU-003',
        quantity: 50,
        unitPrice: 85.0,
        totalValue: 4250.0,
      },
    ],
    totalProductionValue: 4250.0,
    totalUnits: 50,
    remainingStock: {
      'Aluminum Sheet': 0,
    },
  };

  it('should complete a full Create RM → Stock → Product → BOM → Production flow', () => {
    // -------------------------------------------------------
    // Step 1: Create Raw Material
    // -------------------------------------------------------
    cy.intercept('GET', '**/api/v1/raw-materials*', {
      statusCode: 200,
      body: [],
    }).as('getEmptyMaterials');

    cy.intercept('POST', '**/api/v1/raw-materials', {
      statusCode: 201,
      body: newMaterial,
    }).as('createMaterial');

    cy.visit('/raw-materials');
    cy.wait('@getEmptyMaterials');

    cy.getByTestId('add-material-btn').click();
    cy.fillField('Name', 'Aluminum Sheet');
    cy.fillField('Code', 'ALU-SHT-004');
    cy.fillField('Description', 'Thin aluminum sheets for housing');
    cy.selectOption('Unit', 'KILOGRAM');
    cy.fillField('Unit Cost', '12.5');

    cy.contains('button', 'Create').click();
    cy.wait('@createMaterial');
    cy.contains('Raw material created successfully').should('be.visible');

    // -------------------------------------------------------
    // Step 2: Adjust Stock
    // -------------------------------------------------------
    const materialWithStock = { ...newMaterial, stockQuantity: 100 };

    cy.intercept('GET', '**/api/v1/raw-materials*', {
      statusCode: 200,
      body: [newMaterial],
    }).as('getMaterialsList');

    cy.intercept('PATCH', '**/api/v1/raw-materials/4/stock*', {
      statusCode: 200,
      body: materialWithStock,
    }).as('adjustStock');

    // Reload to get the material list with the new item
    cy.visit('/raw-materials');
    cy.wait('@getMaterialsList');

    cy.getByTestId('material-row-4').within(() => {
      cy.getByTestId('adjust-stock-btn').click();
    });

    cy.fillField('Quantity', '100');
    cy.contains('button', 'Adjust').click();
    cy.wait('@adjustStock');
    cy.contains('Stock adjusted successfully').should('be.visible');

    // -------------------------------------------------------
    // Step 3: Create Product
    // -------------------------------------------------------
    cy.intercept('GET', '**/api/v1/products*', {
      statusCode: 200,
      body: [],
    }).as('getEmptyProducts');

    cy.intercept('POST', '**/api/v1/products', {
      statusCode: 201,
      body: newProduct,
    }).as('createProduct');

    cy.visit('/products');
    cy.wait('@getEmptyProducts');

    cy.getByTestId('add-product-btn').click();
    cy.fillField('Name', 'Aluminum Housing');
    cy.fillField('SKU', 'HSG-ALU-003');
    cy.fillField('Description', 'Protective housing for sensors');
    cy.fillField('Unit Price', '85');

    cy.contains('button', 'Create').click();
    cy.wait('@createProduct');
    cy.contains('Product created successfully').should('be.visible');

    // -------------------------------------------------------
    // Step 4: Add Material to Product BOM
    // -------------------------------------------------------
    cy.intercept('GET', '**/api/v1/products*', {
      statusCode: 200,
      body: [newProduct],
    }).as('getProductsWithNew');

    cy.intercept('GET', '**/api/v1/raw-materials*', {
      statusCode: 200,
      body: [materialWithStock],
    }).as('getMaterialsForBom');

    cy.intercept('POST', '**/api/v1/products/3/materials', {
      statusCode: 200,
      body: productWithBom,
    }).as('addMaterialToBom');

    cy.visit('/products');
    cy.wait('@getProductsWithNew');

    cy.getByTestId('product-row-3').within(() => {
      cy.getByTestId('manage-bom-btn').click();
    });

    cy.wait('@getMaterialsForBom');

    // Select the raw material
    cy.get('.MuiDrawer-root').within(() => {
      cy.contains('label', 'Raw Material')
        .parent()
        .find('[role="combobox"]')
        .click();
    });
    cy.get('[role="listbox"]').contains('Aluminum Sheet').click();

    // Set quantity
    cy.get('.MuiDrawer-root').within(() => {
      cy.contains('label', 'Qty').parent().find('input').clear().type('2');
    });

    cy.get('[title="Add to BOM"]').click();
    cy.wait('@addMaterialToBom');
    cy.contains('Material added to BOM').should('be.visible');

    // -------------------------------------------------------
    // Step 5: Calculate Production
    // -------------------------------------------------------
    cy.intercept('POST', '**/api/v1/production/calculate', {
      statusCode: 200,
      body: productionPlan,
    }).as('calculateProduction');

    cy.visit('/production');

    cy.contains('button', 'Calculate Production Plan').click();
    cy.wait('@calculateProduction');

    // -------------------------------------------------------
    // Step 6: Verify Results
    // -------------------------------------------------------
    cy.contains('$4,250.00').should('be.visible');
    cy.contains('50').should('be.visible');
    cy.contains('Aluminum Housing').should('be.visible');
    cy.contains('HSG-ALU-003').should('be.visible');
    cy.contains('Aluminum Sheet: 0').should('be.visible');
  });
});

describe('CRUD Check', () => {
  it('should edit a product name', () => {
    cy.mockProducts();

    const updatedProduct = {
      id: 1,
      name: 'Renamed Component',
      description: 'High-quality electronic component for industrial use',
      sku: 'COMP-XYZ-001',
      unitPrice: 29.99,
      stockQuantity: 100,
      active: true,
      createdAt: '2026-01-15T10:30:00',
      updatedAt: '2026-02-02T10:00:00',
      materials: [
        { rawMaterialId: 1, quantityRequired: 2.5 },
        { rawMaterialId: 2, quantityRequired: 1.0 },
      ],
    };

    cy.intercept('PUT', '**/api/v1/products/1', {
      statusCode: 200,
      body: updatedProduct,
    }).as('updateProduct');

    cy.visit('/products');
    cy.wait('@getProducts');

    cy.getByTestId('product-row-1').within(() => {
      cy.getByTestId('edit-product-btn').click();
    });

    cy.fillField('Name', 'Renamed Component');
    cy.contains('button', 'Save Changes').click();
    cy.wait('@updateProduct');
    cy.contains('Product updated successfully').should('be.visible');
  });

  it('should delete an unused raw material', () => {
    cy.mockRawMaterials();

    cy.intercept('DELETE', '**/api/v1/raw-materials/3', {
      statusCode: 204,
    }).as('deleteMaterial');

    cy.visit('/raw-materials');
    cy.wait('@getRawMaterials');

    // Delete the inactive "Plastic Resin" (id=3) which is not used in any BOM
    cy.getByTestId('material-row-3').within(() => {
      cy.getByTestId('delete-material-btn').click();
    });

    cy.contains('button', 'Delete').click();
    cy.wait('@deleteMaterial');
    cy.contains('Raw material deleted successfully').should('be.visible');
  });
});

describe('Validation', () => {
  it('should not allow creating a product without a name', () => {
    cy.mockProducts();

    cy.visit('/products');
    cy.wait('@getProducts');

    cy.getByTestId('add-product-btn').click();

    // Fill everything except name
    cy.fillField('SKU', 'TEST-001');
    cy.fillField('Unit Price', '10');

    // Create button should remain disabled
    cy.contains('button', 'Create').should('be.disabled');
  });

  it('should not allow creating a raw material without a name', () => {
    cy.mockRawMaterials();

    cy.visit('/raw-materials');
    cy.wait('@getRawMaterials');

    cy.getByTestId('add-material-btn').click();

    // Fill everything except name
    cy.fillField('Code', 'TEST-001');
    cy.fillField('Unit Cost', '10');

    cy.contains('button', 'Create').should('be.disabled');
  });
});

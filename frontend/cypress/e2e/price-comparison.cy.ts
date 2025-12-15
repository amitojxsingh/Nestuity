describe('Price Comparison flow', () => {
  const mockBabyProduct = {
    id: 1,
    name: 'Baby Bottle',
    brand: 'Nuby',
    category: 'Feeding',
    retailer: 'Amazon',
    priceHistory: [
      { retailer: 'Amazon', price: 12.99, date: '2025-11-26T08:00:00' },
    ],
  };

  beforeEach(() => {
    cy.intercept('GET', '**/api/baby-products', [mockBabyProduct]).as('getProducts');

    cy.visit('/auth/login');
    cy.get('input[type="email"]').type('user@example.com');
    cy.get('input[type="password"]').type('User1!');
    cy.get('button[type="submit"]').click();

    cy.url({ timeout: 10000 }).should('include', '/dashboard');
  });

  it('shows loading state and product grid', () => {
    cy.visit('/price-comparison');
    cy.get('p').contains('Loading products...', { timeout: 10000 }).should('exist');
    cy.wait('@getProducts');
    cy.contains('Baby Bottle', { timeout: 10000 }).should('exist');
  });

  it('can search for a product', () => {
    cy.visit('/price-comparison');
    cy.wait('@getProducts');
    cy.get('input[placeholder*="Search products"]').type('Bottle');
    cy.get('button').contains('Search').click();
    cy.contains('Baby Bottle', { timeout: 10000 }).should('exist');
  });

  it('can reset filters', () => {
    cy.visit('/price-comparison');
    cy.wait('@getProducts');
    cy.get('button').contains('Reset Filters').click();
    cy.contains('Baby Bottle', { timeout: 10000 }).should('exist');
  });
});

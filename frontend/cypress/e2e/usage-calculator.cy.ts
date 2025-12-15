describe('Usage Calculator - View Only', () => {
  const mockUser = {
    id: 1,
    firstName: 'Test',
    lastName: 'Last',
    email: 'testuser@example.com',
  };

  const mockBaby = {
    id: 1,
    name: 'Baby',
    dob: '2025-01-01',
    weight: 3,
    diaperSize: '2',
    dailyUsage: 6,
    diapersPerBox: 24,
  };

  const mockDiaperUsage = {
    daysLeft: 7,
    recommendedPurchase: 2,
    message: 'You have enough diapers for the week.',
  };

  const mockInventory = [
    {
      supplyName: 'Diapers',
      totalSingleQuantity: 50,
      totalUnitQuantity: 2,
      unitConversion: 24,
    },
  ];

  beforeEach(() => {
    // Login intercepts
    cy.intercept('POST', '**/api/auth/callback/credentials*').as('login');
    cy.intercept('GET', '**/api/users/1', mockUser).as('getUser');

    // Baby intercepts
    cy.intercept('GET', '**/api/babies/1', mockBaby).as('getBaby');
    cy.intercept('GET', '**/api/babies/user/1', [mockBaby]).as('getBabiesByUser');
    cy.intercept('GET', '**/api/babies/1/diaper-usage', mockDiaperUsage).as('getDiaperUsage');

    // Inventory intercepts
    cy.intercept('GET', '**/api/inventory/user/1', mockInventory).as('getUserInventory');

    // Visit and login
    cy.visit('/auth/login');
    cy.get('input[type="email"]').type('testuser@example.com');
    cy.get('input[type="password"]').type('Test123!');
    cy.contains('button', 'Sign In').click();
    cy.wait('@login');
    cy.url().should('include', '/dashboard');

    cy.visit('/usage-calculator');
    cy.wait([
      '@getBabiesByUser',
      '@getBaby',
      '@getDiaperUsage',
      '@getUserInventory',
    ]);
  });

  it('renders all main sections of the calculator', () => {
    // Header
    cy.contains('Usage Calculator').should('exist');
    cy.contains(mockBaby.name).should('exist');

    // Baby stats grid
    cy.contains("Baby's Age").should('exist');
    cy.contains("Average Diaper Usage per Day").should('exist');
    cy.contains("Baby's Weight").should('exist');
    cy.contains("Diaper Size").should('exist');
    cy.contains("Number of Diapers per Box").should('exist');
    cy.contains("Diapers at Home").should('exist');

    // Diaper status widget
    cy.contains('Diaper Status').should('exist');
    // Check Estimated Days Remaining by exact value
    cy.get('div')
      .contains('Estimated Days Remaining')
      .parent()
      .within(() => {
        cy.get('span.text-4xl').should('contain', mockDiaperUsage.daysLeft.toString());
        cy.get('span.text-xl').should('contain', 'days');
      });

    // Suggested purchase amount
    cy.contains('Suggested purchase amount').parent().within(() => {
      cy.get('span.text-3xl').should('contain', mockDiaperUsage.recommendedPurchase.toString());
    });

    // Message
    cy.contains(mockDiaperUsage.message).should('exist');
  });
});

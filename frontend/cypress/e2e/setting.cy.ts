describe('Baby Settings flow (with PUT intercept)', () => {
  const mockUser = {
    id: 1,
    firstName: 'Test',
    lastName: 'Last',
    email: 'testuser@example.com',
    babies: [
      { id: 1, name: 'Baby', dob: '2025-01-01', weight: 2.0, diaperSize: '1' },
    ],
    preferences: {},
  };

  beforeEach(() => {
    cy.intercept('POST', '**/api/auth/callback/credentials*').as('login');
    cy.intercept('GET', '**/api/users/1', mockUser).as('getUser');

    // Intercept the PUT request to /api/babies/1 and mock the response
    cy.intercept('PUT', '**/api/babies/1', (req) => {
      // Optionally inspect the request body
      console.log('Intercepted PUT body:', req.body);

      req.reply({
        statusCode: 200,
        body: { ...mockUser.babies[0], ...req.body }, // merge changes
      });
    }).as('updateBaby');

    cy.visit('/auth/login');

    cy.get('input[type="email"]').type('testuser@example.com');
    cy.get('input[type="password"]').type('Test123!');
    cy.contains('button', 'Sign In').click();

    cy.wait('@login');
    cy.wait(5000);
    cy.url().should('include', '/dashboard');

    cy.visit('/settings');
    cy.wait('@getUser');
  });

  it('updates baby settings successfully', () => {
    cy.contains('Baby Settings').click();

    cy.get('#babyName').clear().type('New Baby Name');
    cy.get('#birthDate').clear().type('2025-05-05');
    cy.get('#weight').clear().type('3');
    cy.get('#diaperSize').select('2');

    cy.contains('button', 'Save Changes').click();

    // Wait for the PUT call to complete and validate the request body
    cy.wait('@updateBaby').its('request.body').should((body) => {
      expect(body.name).to.eq('New Baby Name');
      expect(body.dob).to.eq('2025-05-05');
      expect(body.weight).to.eq(3);
      expect(body.diaperSize).to.eq('2');
    });

    // Verify the UI shows success
    cy.contains('Baby updated successfully!').should('exist');
  });
});

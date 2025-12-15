describe('User registration flow', () => {
  beforeEach(() => {
    cy.visit('http://localhost:3000/auth/login');
    cy.get('a.text-\\[var\\(--color-primary\\)\\]').click();
  });

  it('registers a new user successfully', () => {
    const randomEmail = `testuser${Date.now()}@email.com`;

    // Intercept the registration API
    cy.intercept('POST', 'http://localhost:8080/api/users', {
      statusCode: 201,
      body: { id: 123, email: randomEmail }
    }).as('registerUser');

    cy.get('input[placeholder="First Name"]').type('Test');
    cy.get('input[placeholder="Last Name"]').type('Last');
    cy.get('input[placeholder="Email"]').type(randomEmail);
    cy.get('input[placeholder="Password"]').type('Test123!');
    cy.get('input[placeholder="Confirm Password"]').type('Test123!');

    cy.get('button[type="submit"]').click();

    // Wait for the intercepted request
    cy.wait('@registerUser');

    // ---- Baby Stats Page ----
    // Intercept the baby create API
    cy.intercept('POST', 'http://localhost:8080/api/babies', {
      statusCode: 201,
      body: { id: 1, name: 'Baby', dob: '2025-01-01', weight: 2.0 }
    }).as('createBaby');

    // Fill in baby name
    cy.get('input[placeholder="Baby\'s Name"]').type('Baby');

    // Fill in baby DOB (today or earlier)
    const today = new Date().toISOString().split('T')[0];
    cy.get('input[type="date"]').type(today);

    // Fill in baby weight
    cy.get('input[type="number"]').type('2');

    // Submit the form
    cy.get('button[type="submit"]').click();
    cy.wait('@createBaby');

    // Verify we redirected to dashboard
    cy.url().should('include', '/dashboard');
  });
});

describe('User login flow', () => {
  it('logs in the user successfully', () => {
    cy.visit('http://localhost:3000/auth/login');
    cy.get('input[type="email"]').type('napoleon@test.com');
    cy.get('input[type="password"]').type('Napoleon@1');
    cy.get('button.text-white').click();
    cy.wait(5000);

    cy.url().should('include', '/dashboard');
  });
});

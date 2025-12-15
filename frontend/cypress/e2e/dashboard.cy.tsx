describe('User login and dashboard flow', () => {
  const mockBaby = { id: 1, name: 'Napoleon', dob: '2025-01-01' };
  const mockTasks = {
    daily: [{ id: 101, title: 'Feed baby', type: 'TASK', range: 'TODAY' }],
    weekly: [],
    vaccinations: [],
  };
  const mockMilestone = { id: 201, title: 'First Smile', description: 'Baby smiles for the first time' };

  beforeEach(() => {
    // Intercepts (register FIRST)
    cy.intercept('POST', '**/api/auth/callback/credentials*').as('login');
    cy.intercept('GET', '**/api/babies/user/*', [mockBaby]).as('getBaby');
    cy.intercept('GET', '**/api/reminders/baby/*/upcoming*', Object.values(mockTasks).flat()).as('getReminders');
    cy.intercept('GET', '**/api/reminders/baby/*/current', mockMilestone).as('getMilestone');

    // Visit login
    cy.visit('/auth/login');

    // Valid credentials
    cy.get('input[type="email"]').type('napoleon@test.com');
    cy.get('input[type="password"]').type('Napoleon@1');

    // Click login
    cy.contains('button', 'Sign In').click();
    cy.wait(5000);

    // Should now be on dashboard
    cy.url().should('include', '/dashboard');

    // The dashboard API calls should fire
    cy.wait('@getBaby');
    cy.wait('@getReminders');
    cy.wait('@getMilestone');
  });

  it('logs in and shows dashboard content', () => {
    cy.contains('Hello Napoleon').should('exist');
    cy.contains('Remaining Diapers').should('exist');
  });

  it('shows tasks tabs and can switch between them', () => {
    cy.contains('button', 'Upcoming Week').click();
    cy.contains('button', 'Vaccinations').click();
    cy.contains('button', 'Today').click();
    cy.contains('Feed baby').should('exist');
  });
});

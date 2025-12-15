describe('Intel Reminders flow', () => {
  // Mock data
  const mockBaby = { id: 1, name: 'Napoleon', dob: '2025-01-01' };
  const mockTasks = [
    {
      id: 101,
      babyId: 1,
      type: 'TASK',
      title: 'Feed baby',
      description: 'Feed baby breakfast',
      frequency: 'DAILY',
      occurrence: 1,
      requiresAction: true,
      notes: null,
      completedOn: null,
      nextDue: '2025-11-26T09:00:00',
      range: 'TODAY',
      startDate: '2025-11-26T08:00:00',
      userCreated: false,
    },
  ];
  const mockMilestone = { id: 201, title: 'First Smile', description: 'Baby smiles for the first time' };

  beforeEach(() => {
    // Intercept API calls
    cy.intercept('GET', '**/api/babies/user/*', [mockBaby]).as('getBaby');
    cy.intercept('GET', '**/api/reminders/baby/*/current', mockMilestone).as('getMilestone');
    // Because we promise them together, if we do not call the tasks, milestones will not load
    // TODO: fix tasks not loading
    cy.intercept('GET', '**/api/reminders/baby/*/upcoming*', mockTasks).as('getReminders');

    // Login via UI
    cy.visit('/auth/login');
    cy.get('input[type="email"]').type('user@example.com');
    cy.get('input[type="password"]').type('User1!');
    cy.get('button[type="submit"]').click();

    // Ensure login completes
    cy.wait('@getBaby');
    cy.wait('@getMilestone');
  });

  it('shows loading state and milestone widget', () => {
    // Visit Intel Reminders page
    cy.visit('/intel-reminders');

    // Loading state should appear first
    cy.get('p').contains('Loading...').should('exist');

    // Wait for milestone widget
    cy.get('h2.text-accent-primary')
      .contains(mockMilestone.title)
      .should('exist');
    cy.get('p.text-accent-secondary')
      .contains(mockMilestone.description)
      .should('exist');
  });

});

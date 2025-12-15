declare namespace Cypress {
  interface Chainable {
    loginByMockSession(): Chainable<void>;
  }
}

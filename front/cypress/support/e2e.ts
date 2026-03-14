import '@cypress/code-coverage/support'
Cypress.Commands.add('loginByApi', () => {
  window.localStorage.setItem('mdd_token', 'fake-jwt-token');
});

declare global {
  namespace Cypress {
    interface Chainable {
      loginByApi(): Chainable<void>;
    }
  }
}

export {};

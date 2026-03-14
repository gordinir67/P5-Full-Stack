describe('Authentification et navigation', () => {
  it('connecte l’utilisateur et affiche les articles', () => {
    cy.intercept('POST', '**/auth/login', {
      statusCode: 200,
      body: { token: 'fake-jwt-token' },
    }).as('login');

    cy.intercept('GET', '**/articles*', {
      statusCode: 200,
      body: [
        {
          id: 1,
          title: 'Premier article',
          description: 'Contenu de démonstration',
          createdAt: '2025-01-15T00:00:00.000Z',
          user: { name: 'Alice' },
          theme: { title: 'Angular' },
        },
      ],
    }).as('getArticles');

    cy.visit('/login');
    cy.get('#login-email').type('john@doe.dev');
    cy.get('#login-password').type('Secret123!');
    cy.contains('button', 'Se connecter').click();

    cy.wait('@login');
    cy.url().should('include', '/articles');
    cy.wait('@getArticles');
    cy.contains('Premier article').should('be.visible');
  });

  it('redirige un utilisateur authentifié depuis /login vers /articles', () => {
    cy.loginByApi();
    cy.intercept('GET', '**/articles*', { statusCode: 200, body: [] }).as('getArticles');

    cy.visit('/login');

    cy.url().should('include', '/articles');
    cy.wait('@getArticles');
  });

  it('ne soumet pas le login quand le formulaire est invalide', () => {
    cy.intercept('POST', '**/auth/login').as('login');

    cy.visit('/login');

    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('form').then(($form) => {
      ($form[0] as HTMLFormElement).requestSubmit();
    });

    cy.get('@login.all').should('have.length', 0);
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it("ne soumet pas l'inscription quand le formulaire est invalide", () => {
    cy.intercept('POST', '**/auth/register').as('register');

    cy.visit('/register');

    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('form').then(($form) => {
      ($form[0] as HTMLFormElement).requestSubmit();
    });

    cy.get('@register.all').should('have.length', 0);
    cy.get('button[type="submit"]').should('be.disabled');
  });
});

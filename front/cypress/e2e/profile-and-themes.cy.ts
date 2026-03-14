describe('Profil et thèmes', () => {
  beforeEach(() => {
    cy.loginByApi();
  });

  it("affiche les thèmes et permet de s'abonner", () => {
    cy.intercept('GET', '**/api/themes', {
      statusCode: 200,
      body: [{ id: 5, title: 'RxJS', description: 'Programmation réactive', subscribed: false }],
    }).as('getThemes');

    cy.intercept('POST', '**/api/themes/5/subscribe', {
      statusCode: 200,
      body: {},
    }).as('subscribeTheme');

    cy.visit('/themes');
    cy.wait('@getThemes');
    cy.contains('RxJS').should('be.visible');
    cy.contains('button', "S’abonner").click();

    cy.wait('@subscribeTheme');
  });

  it('charge le profil et permet la mise à jour', () => {
    cy.intercept('GET', '**/api/me', {
      statusCode: 200,
      body: { id: 1, name: 'John', email: 'john@doe.dev' },
    }).as('getMe');

    cy.intercept('GET', '**/api/themes', {
      statusCode: 200,
      body: [{ id: 2, title: 'Angular', description: 'Framework', subscribed: true }],
    }).as('getThemes');

    cy.intercept('PUT', '**/api/me', {
      statusCode: 200,
      body: { id: 1, name: 'Johnny', email: 'johnny@doe.dev' },
    }).as('updateMe');

    cy.intercept('DELETE', '**/api/themes/2/subscribe', {
      statusCode: 200,
      body: {},
    }).as('unsubscribeTheme');

    cy.visit('/me');
    cy.wait('@getMe');
    cy.wait('@getThemes');

    cy.get('input[formcontrolname="name"]').clear().type('Johnny');
    cy.get('input[formcontrolname="email"]').clear().type('johnny@doe.dev');
    cy.get('input[formcontrolname="password"]').type('Secret123!');
    cy.contains('button', 'Sauvegarder').click();

    cy.wait('@updateMe');
    cy.contains('Profil mis à jour.').should('be.visible');

    cy.contains('button', 'Se désabonner').click();
    cy.wait('@unsubscribeTheme');
  });

  it('ne soumet pas le profil si le formulaire est invalide', () => {
    cy.intercept('GET', '**/api/me', {
      statusCode: 200,
      body: { id: 1, name: 'John', email: 'john@doe.dev' },
    }).as('getMe');

    cy.intercept('GET', '**/api/themes', {
      statusCode: 200,
      body: [{ id: 2, title: 'Angular', description: 'Framework', subscribed: true }],
    }).as('getThemes');

    cy.intercept('PUT', '**/api/me').as('updateMe');

    cy.visit('/me');
    cy.wait('@getMe');
    cy.wait('@getThemes');

    cy.get('input[formcontrolname="password"]').clear();
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('form').trigger('submit');

    cy.get('@updateMe.all').should('have.length', 0);
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('ignore la double soumission du profil', () => {
    cy.intercept('GET', '**/api/me', {
      statusCode: 200,
      body: { id: 1, name: 'John', email: 'john@doe.dev' },
    }).as('getMe');

    cy.intercept('GET', '**/api/themes', {
      statusCode: 200,
      body: [{ id: 2, title: 'Angular', description: 'Framework', subscribed: true }],
    }).as('getThemes');

    cy.intercept('PUT', '**/api/me', (req) => {
      req.reply({
        delay: 800,
        statusCode: 200,
        body: { id: 1, name: 'Johnny', email: 'johnny@doe.dev' },
      });
    }).as('updateMe');

    cy.visit('/me');
    cy.wait('@getMe');
    cy.wait('@getThemes');

    cy.get('input[formcontrolname="name"]').clear().type('Johnny');
    cy.get('input[formcontrolname="email"]').clear().type('johnny@doe.dev');
    cy.get('input[formcontrolname="password"]').clear().type('Secret123!');

    cy.contains('button', 'Sauvegarder').should('not.be.disabled').click();
    cy.contains('button', 'Sauvegarder').trigger('click', { force: true });

    cy.wait('@updateMe');
    cy.get('@updateMe.all').should('have.length', 1);
    cy.contains('Profil mis à jour.').should('be.visible');
  });

  it('empêche la sauvegarde avec un mot de passe vide puis autorise la sauvegarde une fois le formulaire valide', () => {
    cy.intercept('GET', '**/api/me', {
      statusCode: 200,
      body: { id: 1, name: 'John', email: 'john@doe.dev' },
    }).as('getMe');

    cy.intercept('GET', '**/api/themes', {
      statusCode: 200,
      body: [{ id: 2, title: 'Angular', description: 'Framework', subscribed: true }],
    }).as('getThemes');

    cy.intercept('PUT', '**/api/me', {
      statusCode: 200,
      body: { id: 1, name: 'Johnny', email: 'johnny@doe.dev' },
    }).as('updateMe');

    cy.visit('/me');
    cy.wait('@getMe');
    cy.wait('@getThemes');

    cy.get('input[formcontrolname="name"]').clear().type('Johnny');
    cy.get('input[formcontrolname="email"]').clear().type('johnny@doe.dev');
    cy.get('input[formcontrolname="password"]').clear();

    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('button[type="submit"]').trigger('click', { force: true });
    cy.get('@updateMe.all').should('have.length', 0);

    cy.get('input[formcontrolname="password"]').type('Secret123!');
    cy.get('button[type="submit"]').should('not.be.disabled').click();

    cy.wait('@updateMe');
    cy.get('@updateMe.all').should('have.length', 1);
    cy.contains('Profil mis à jour.').should('be.visible');
  });
});

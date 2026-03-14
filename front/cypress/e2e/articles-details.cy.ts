describe('Parcours article', () => {
  beforeEach(() => {
    cy.loginByApi();
  });

  it('ouvre un article puis ajoute un commentaire', () => {
    cy.intercept('GET', '**/articles?sort=*', {
      statusCode: 200,
      body: [
        {
          id: 12,
          title: 'Angular 19',
          description: 'Standalone partout',
          createdAt: '2025-01-15T00:00:00.000Z',
          user: { name: 'Alice' },
          theme: { title: 'Angular' },
        },
      ],
    }).as('getArticles');

    cy.intercept('GET', '**/articles/12', {
      statusCode: 200,
      body: {
        id: 12,
        title: 'Angular 19',
        description: 'Standalone partout',
        createdAt: '2025-01-15T00:00:00.000Z',
        user: { name: 'Alice' },
        theme: { title: 'Angular' },
        comments: [],
      },
    }).as('getArticle');

    cy.intercept('POST', '**/articles/12/comments', {
      statusCode: 200,
      body: {
        id: 100,
        description: 'Très bon article',
        user: { name: 'John' },
      },
    }).as('postComment');

    cy.visit('/articles');
    cy.wait('@getArticles');
    cy.contains('Premier article').should('not.exist');
    cy.contains('Angular 19').click();

    cy.wait('@getArticle');
    cy.get('textarea[formcontrolname="description"]').type('Très bon article');
    cy.get('button[aria-label="Envoyer"]').click();

    cy.wait('@postComment');
    cy.contains('Très bon article').should('be.visible');
    cy.contains('John').should('be.visible');
  });

  it('crée un article', () => {
    cy.intercept('GET', '**/themes', {
      statusCode: 200,
      body: [{ id: 1, title: 'Angular', description: 'Framework', subscribed: false }],
    }).as('getThemes');

    cy.intercept('POST', '**/articles', {
      statusCode: 200,
      body: { id: 42 },
    }).as('createArticle');

    cy.visit('/articles/create');
    cy.wait('@getThemes');

    cy.get('mat-select[formcontrolname="themeId"]').click();
    cy.get('mat-option').contains('Angular').click();
    cy.get('input[formcontrolname="title"]').type('Mon nouvel article');
    cy.get('textarea[formcontrolname="description"]').type(
      'Un contenu suffisamment complet pour le test e2e.'
    );
    cy.contains('button', 'Créer').click();

    cy.wait('@createArticle');
    cy.url().should('include', '/articles/42');
  });

  it('ne soumet pas la création d’article si le formulaire est invalide', () => {
    cy.intercept('GET', '**/themes', {
      statusCode: 200,
      body: [{ id: 1, title: 'Angular', description: 'Framework', subscribed: false }],
    }).as('getThemes');

    cy.intercept('POST', '**/articles').as('createArticle');

    cy.visit('/articles/create');
    cy.wait('@getThemes');

    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('form').trigger('submit');

    cy.get('@createArticle.all').should('have.length', 0);
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('ignore le double envoi de commentaire et gère un article sans commentaires initiaux', () => {
    cy.intercept('GET', '**/articles?sort=*', {
      statusCode: 200,
      body: [
        {
          id: 12,
          title: 'Angular 19',
          description: 'Standalone partout',
          createdAt: '2025-01-15T00:00:00.000Z',
          user: { name: 'Alice' },
          theme: { title: 'Angular' },
        },
      ],
    }).as('getArticles');

    cy.intercept('GET', '**/articles/12', {
      statusCode: 200,
      body: {
        id: 12,
        title: 'Angular 19',
        description: 'Standalone partout',
        createdAt: '2025-01-15T00:00:00.000Z',
        user: { name: 'Alice' },
        theme: { title: 'Angular' },
      },
    }).as('getArticle');

    cy.intercept('POST', '**/articles/12/comments', (req) => {
      req.reply({
        delay: 800,
        statusCode: 200,
        body: {
          id: 101,
          description: 'Deuxième commentaire',
          user: { name: 'John' },
        },
      });
    }).as('postComment');

    cy.visit('/articles');
    cy.wait('@getArticles');
    cy.contains('Angular 19').click();

    cy.wait('@getArticle');

    cy.get('textarea[formcontrolname="description"]').type('Deuxième commentaire');

    cy.get('button[aria-label="Envoyer"]').click();
    cy.get('button[aria-label="Envoyer"]').trigger('click', { force: true });

    cy.wait('@postComment');
    cy.get('@postComment.all').should('have.length', 1);
    cy.contains('Deuxième commentaire').should('be.visible');
    cy.contains('John').should('be.visible');
  });

  it('ne soumet pas un commentaire vide puis ajoute le premier commentaire', () => {
    cy.intercept('GET', '**/articles?sort=*', {
      statusCode: 200,
      body: [
        {
          id: 12,
          title: 'Angular 19',
          description: 'Standalone partout',
          createdAt: '2025-01-15T00:00:00.000Z',
          user: { name: 'Alice' },
          theme: { title: 'Angular' },
        },
      ],
    }).as('getArticles');

    cy.intercept('GET', '**/articles/12', {
      statusCode: 200,
      body: {
        id: 12,
        title: 'Angular 19',
        description: 'Standalone partout',
        createdAt: '2025-01-15T00:00:00.000Z',
        user: { name: 'Alice' },
        theme: { title: 'Angular' },
      },
    }).as('getArticle');

    cy.intercept('POST', '**/articles/12/comments', {
      statusCode: 200,
      body: {
        id: 102,
        description: 'Premier commentaire utile',
        user: { name: 'John' },
      },
    }).as('postComment');

    cy.visit('/articles');
    cy.wait('@getArticles');
    cy.contains('Angular 19').click();

    cy.wait('@getArticle');

    cy.get('button[aria-label="Envoyer"]').should('be.disabled');
    cy.get('button[aria-label="Envoyer"]').trigger('click', { force: true });
    cy.get('@postComment.all').should('have.length', 0);

    cy.get('textarea[formcontrolname="description"]').type('Premier commentaire utile');
    cy.get('button[aria-label="Envoyer"]').should('not.be.disabled').click();

    cy.wait('@postComment');
    cy.contains('Premier commentaire utile').should('be.visible');
    cy.contains('John').should('be.visible');
  });
});

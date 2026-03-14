import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ArticleCreateComponent } from './article-create.component';
import { ThemeService } from '../../services/theme.service';
import { ArticleService } from '../../services/article.service';
import { AuthService } from '../../services/auth.service';

describe('ArticleCreateComponent', () => {
  let fixture: ComponentFixture<ArticleCreateComponent>;
  let component: ArticleCreateComponent;
  let themesApi: { list: jest.Mock };
  let articlesApi: { create: jest.Mock };
  let router: Router;

  beforeEach(async () => {
    themesApi = { list: jest.fn().mockReturnValue(of([{ id: 1, title: 'Angular', description: 'Framework', subscribed: false }])) };
    articlesApi = { create: jest.fn().mockReturnValue(of({ id: 42 })) };

    await TestBed.configureTestingModule({
      imports: [ArticleCreateComponent, NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ThemeService, useValue: themesApi },
        { provide: ArticleService, useValue: articlesApi },
        { provide: AuthService, useValue: { logout: jest.fn(), isLoggedIn: jest.fn().mockReturnValue(true) } },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(ArticleCreateComponent);
    component = fixture.componentInstance;
  });

  it('loads themes on init', () => {
    fixture.detectChanges();

    expect(themesApi.list).toHaveBeenCalled();
    expect(component.themes).toHaveLength(1);
    expect(component.isLoadingThemes).toBe(false);
  });

  it('marks form as touched when invalid', () => {
    fixture.detectChanges();
    const markAllAsTouchedSpy = jest.spyOn(component.form, 'markAllAsTouched');

    component.submit();

    expect(markAllAsTouchedSpy).toHaveBeenCalled();
    expect(articlesApi.create).not.toHaveBeenCalled();
  });

  it('creates article and redirects to detail page', () => {
    fixture.detectChanges();
    component.form.setValue({ themeId: 1, title: 'Mon titre', description: 'Mon contenu' });

    component.submit();

    expect(articlesApi.create).toHaveBeenCalledWith({ themeId: 1, title: 'Mon titre', description: 'Mon contenu' });
    expect(router.navigate).toHaveBeenCalledWith(['/articles', 42]);
    expect(component.isSubmitting).toBe(false);
  });

  it('shows backend error when create fails', () => {
    fixture.detectChanges();
    articlesApi.create.mockReturnValue(throwError(() => ({ error: { message: 'Erreur création' } })));
    component.form.setValue({ themeId: 1, title: 'Mon titre', description: 'Mon contenu' });

    component.submit();

    expect(component.error).toBe('Erreur création');
    expect(component.isSubmitting).toBe(false);
  });
});

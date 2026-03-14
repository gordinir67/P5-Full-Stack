import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ArticleDetailComponent } from './article-detail.component';
import { ArticleService } from '../../services/article.service';
import { AuthService } from '../../services/auth.service';

describe('ArticleDetailComponent', () => {
  let fixture: ComponentFixture<ArticleDetailComponent>;
  let component: ArticleDetailComponent;
  let api: { getById: jest.Mock; addComment: jest.Mock };

  const article = {
    id: 12,
    title: 'Angular 19',
    description: 'Standalone everywhere',
    createdAt: '2025-01-01',
    user: { name: 'Jane' },
    theme: { title: 'Angular' },
    comments: [{ id: 1, description: 'Top', user: { name: 'John' } }],
  };

  beforeEach(async () => {
    api = {
      getById: jest.fn().mockReturnValue(of(article)),
      addComment: jest.fn().mockReturnValue(of({ id: 2, description: 'Nice', user: { name: 'Alice' } })),
    };

    await TestBed.configureTestingModule({
      imports: [ArticleDetailComponent, NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ArticleService, useValue: api },
        { provide: AuthService, useValue: { logout: jest.fn(), isLoggedIn: jest.fn().mockReturnValue(true) } },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: (key: string) => (key === 'id' ? '12' : null) } } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleDetailComponent);
    component = fixture.componentInstance;
  });

  it('loads article on init', () => {
    fixture.detectChanges();

    expect(api.getById).toHaveBeenCalledWith(12);
    expect(component.article?.title).toBe('Angular 19');
    expect(component.isLoading).toBe(false);
  });

  it('shows backend error when article loading fails', () => {
    api.getById.mockReturnValue(throwError(() => ({ error: { message: 'Introuvable' } })));

    fixture.detectChanges();

    expect(component.error).toBe('Introuvable');
    expect(component.isLoading).toBe(false);
  });

  it('marks comment form as touched when invalid', () => {
    fixture.detectChanges();
    const markAllAsTouchedSpy = jest.spyOn(component.commentForm, 'markAllAsTouched');

    component.sendComment();

    expect(markAllAsTouchedSpy).toHaveBeenCalled();
    expect(api.addComment).not.toHaveBeenCalled();
  });

  it('adds comment to current article', () => {
    fixture.detectChanges();
    component.commentForm.setValue({ description: 'Nice' });

    component.sendComment();

    expect(api.addComment).toHaveBeenCalledWith(12, { description: 'Nice' });
    expect(component.article?.comments).toHaveLength(2);
    expect(component.commentForm.value.description).toBe('');
    expect(component.isSending).toBe(false);
  });

  it('reloads article when adding comment without article in memory', () => {
    fixture.detectChanges();
    component.article = null;
    api.getById.mockClear();
    component.commentForm.setValue({ description: 'Nice' });

    component.sendComment();

    expect(api.getById).toHaveBeenCalledWith(12);
  });
});

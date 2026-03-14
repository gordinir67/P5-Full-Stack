import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ArticlesComponent } from './articles.component';
import { ArticleService } from '../../services/article.service';
import { AuthService } from '../../services/auth.service';

describe('ArticlesComponent', () => {
  let fixture: ComponentFixture<ArticlesComponent>;
  let component: ArticlesComponent;
  let articlesApi: { list: jest.Mock };

  beforeEach(async () => {
    articlesApi = { list: jest.fn().mockReturnValue(of([])) };

    await TestBed.configureTestingModule({
      imports: [ArticlesComponent, NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ArticleService, useValue: articlesApi },
        { provide: AuthService, useValue: { logout: jest.fn(), isLoggedIn: jest.fn().mockReturnValue(true) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticlesComponent);
    component = fixture.componentInstance;
  });

  it('loads articles on init with desc sort by default', () => {
    const items = [{ id: 1, title: 'A', description: 'B', createdAt: '2025-01-01', user: { name: 'Jane' }, theme: { title: 'Angular' } }];
    articlesApi.list.mockReturnValue(of(items));

    fixture.detectChanges();

    expect(articlesApi.list).toHaveBeenCalledWith('desc');
    expect(component.articles).toEqual(items);
    expect(component.isLoading).toBe(false);
  });

  it('switches to asc sort when checkbox is checked', () => {
    fixture.detectChanges();
    articlesApi.list.mockClear();

    component.onSortDirChange({ target: { checked: true } } as unknown as Event);

    expect(component.sort).toBe('asc');
    expect(articlesApi.list).toHaveBeenCalledWith('asc');
  });

  it('shows a generic error when loading fails', () => {
    articlesApi.list.mockReturnValue(throwError(() => new Error('boom')));

    fixture.detectChanges();

    expect(component.error).toBe('Impossible de charger les articles.');
    expect(component.isLoading).toBe(false);
  });
});
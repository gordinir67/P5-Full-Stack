import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ThemesComponent } from './themes.component';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';

describe('ThemesComponent', () => {
  let fixture: ComponentFixture<ThemesComponent>;
  let component: ThemesComponent;
  let themesApi: { list: jest.Mock; subscribe: jest.Mock };

  beforeEach(async () => {
    themesApi = {
      list: jest.fn().mockReturnValue(of([])),
      subscribe: jest.fn().mockReturnValue(of(void 0)),
    };

    await TestBed.configureTestingModule({
      imports: [ThemesComponent, NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ThemeService, useValue: themesApi },
        { provide: AuthService, useValue: { logout: jest.fn(), isLoggedIn: jest.fn().mockReturnValue(true) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ThemesComponent);
    component = fixture.componentInstance;
  });

  it('loads themes on init', () => {
    const themes = [{ id: 1, title: 'Angular', description: 'Framework', subscribed: false }];
    themesApi.list.mockReturnValue(of(themes));

    fixture.detectChanges();

    expect(themesApi.list).toHaveBeenCalled();
    expect(component.themes).toEqual(themes);
    expect(component.isLoading).toBe(false);
  });

  it('does nothing when theme is already subscribed', () => {
    fixture.detectChanges();
    themesApi.subscribe.mockClear();

    component.subscribe({ id: 1, title: 'Angular', description: 'Framework', subscribed: true });

    expect(themesApi.subscribe).not.toHaveBeenCalled();
  });

  it('subscribes then reloads the list', () => {
    fixture.detectChanges();
    themesApi.list.mockClear();

    component.subscribe({ id: 2, title: 'Nest', description: 'Node', subscribed: false });

    expect(themesApi.subscribe).toHaveBeenCalledWith(2);
    expect(themesApi.list).toHaveBeenCalled();
  });

  it('shows backend error when subscribe fails', () => {
    fixture.detectChanges();
    themesApi.subscribe.mockReturnValue(throwError(() => ({ error: { message: 'Erreur abonnement' } })));

    component.subscribe({ id: 2, title: 'Nest', description: 'Node', subscribed: false });

    expect(component.error).toBe('Erreur abonnement');
    expect(component.isLoading).toBe(false);
  });
});
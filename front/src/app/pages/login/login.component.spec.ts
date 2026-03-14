import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let auth: { login: jest.Mock };
  let router: Router;

  beforeEach(async () => {
    auth = { login: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, NoopAnimationsModule],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates the component', () => {
    expect(component).toBeTruthy();
  });

  it('marks form as touched when invalid', () => {
    const markAllAsTouchedSpy = jest.spyOn(component.form, 'markAllAsTouched');

    component.submit();

    expect(markAllAsTouchedSpy).toHaveBeenCalled();
    expect(auth.login).not.toHaveBeenCalled();
  });

  it('logs in and redirects to articles', () => {
    auth.login.mockReturnValue(of(void 0));
    component.form.setValue({ email: 'john@doe.dev', password: 'Secret123!' });

    component.submit();

    expect(auth.login).toHaveBeenCalledWith({ email: 'john@doe.dev', password: 'Secret123!' });
    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
    expect(component.isSubmitting).toBe(false);
    expect(component.error).toBe('');
  });

  it('shows backend error when login fails', () => {
    auth.login.mockReturnValue(throwError(() => ({ error: { message: 'Identifiants invalides' } })));
    component.form.setValue({ email: 'john@doe.dev', password: 'wrong' });

    component.submit();

    expect(component.error).toBe('Identifiants invalides');
    expect(component.isSubmitting).toBe(false);
  });
});

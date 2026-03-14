import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('RegisterComponent', () => {
  let fixture: ComponentFixture<RegisterComponent>;
  let component: RegisterComponent;
  let auth: { register: jest.Mock };
  let router: Router;

  beforeEach(async () => {
    auth = { register: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, NoopAnimationsModule],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates the component', () => {
    expect(component).toBeTruthy();
  });

  it('keeps form invalid with weak password', () => {
    component.form.setValue({ name: 'John', email: 'john@doe.dev', password: 'weak' });

    expect(component.form.invalid).toBe(true);
  });

  it('marks form as touched when invalid', () => {
    const markAllAsTouchedSpy = jest.spyOn(component.form, 'markAllAsTouched');

    component.submit();

    expect(markAllAsTouchedSpy).toHaveBeenCalled();
    expect(auth.register).not.toHaveBeenCalled();
  });

  it('registers and redirects to articles', () => {
    auth.register.mockReturnValue(of(void 0));
    component.form.setValue({ name: 'John', email: 'john@doe.dev', password: 'Secret123!' });

    component.submit();

    expect(auth.register).toHaveBeenCalledWith({
      name: 'John',
      email: 'john@doe.dev',
      password: 'Secret123!',
    });
    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
    expect(component.isSubmitting).toBe(false);
  });

  it('shows backend error when registration fails', () => {
    auth.register.mockReturnValue(throwError(() => ({ error: { message: 'Adresse déjà utilisée' } })));
    component.form.setValue({ name: 'John', email: 'john@doe.dev', password: 'Secret123!' });

    component.submit();

    expect(component.error).toBe('Adresse déjà utilisée');
    expect(component.isSubmitting).toBe(false);
  });
});

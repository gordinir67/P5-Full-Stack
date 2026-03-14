import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { MeComponent } from './me.component';
import { MeService } from '../../services/me.service';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';

describe('MeComponent', () => {
  let fixture: ComponentFixture<MeComponent>;
  let component: MeComponent;
  let meApi: { getMe: jest.Mock; updateMe: jest.Mock };
  let themesApi: { list: jest.Mock; unsubscribe: jest.Mock };

  beforeEach(async () => {
    meApi = {
      getMe: jest.fn().mockReturnValue(of({ id: 1, name: 'John', email: 'john@doe.dev' })),
      updateMe: jest.fn().mockReturnValue(of({ id: 1, name: 'Johnny', email: 'johnny@doe.dev' })),
    };
    themesApi = {
      list: jest.fn().mockReturnValue(of([])),
      unsubscribe: jest.fn().mockReturnValue(of(void 0)),
    };

    await TestBed.configureTestingModule({
      imports: [MeComponent, NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: MeService, useValue: meApi },
        { provide: ThemeService, useValue: themesApi },
        { provide: AuthService, useValue: { logout: jest.fn(), isLoggedIn: jest.fn().mockReturnValue(true) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('loads profile and subscriptions on init', () => {
    themesApi.list.mockReturnValue(
      of([
        { id: 1, title: 'Angular', description: 'Framework', subscribed: true },
        { id: 2, title: 'Nest', description: 'Node', subscribed: false },
      ])
    );

    fixture.detectChanges();

    expect(meApi.getMe).toHaveBeenCalled();
    expect(themesApi.list).toHaveBeenCalled();
    expect(component.form.value.name).toBe('John');
    expect(component.form.value.email).toBe('john@doe.dev');
    expect(component.subscriptions).toEqual([
      { id: 1, title: 'Angular', description: 'Framework', subscribed: true },
    ]);
  });

  it('marks form as touched when save is called with invalid form', () => {
    fixture.detectChanges();
    component.form.patchValue({ password: '' });
    const markAllAsTouchedSpy = jest.spyOn(component.form, 'markAllAsTouched');

    component.save();

    expect(markAllAsTouchedSpy).toHaveBeenCalled();
    expect(meApi.updateMe).not.toHaveBeenCalled();
  });

  it('updates profile and resets password on save', () => {
    fixture.detectChanges();
    component.form.setValue({
      name: 'Johnny',
      email: 'johnny@doe.dev',
      password: 'Secret123!',
    });

    component.save();

    expect(meApi.updateMe).toHaveBeenCalledWith({
      name: 'Johnny',
      email: 'johnny@doe.dev',
      password: 'Secret123!',
    });
    expect(component.success).toBe('Profil mis à jour.');
    expect(component.form.value.password).toBe('');
    expect(component.isSaving).toBe(false);
  });

  it('shows backend error when save fails', () => {
    fixture.detectChanges();
    meApi.updateMe.mockReturnValue(throwError(() => ({ error: { message: 'Erreur profil' } })));
    component.form.setValue({
      name: 'Johnny',
      email: 'johnny@doe.dev',
      password: 'Secret123!',
    });

    component.save();

    expect(component.error).toBe('Erreur profil');
    expect(component.isSaving).toBe(false);
  });

  it('unsubscribes then reloads subscriptions', () => {
    fixture.detectChanges();
    themesApi.list.mockClear();

    component.unsubscribe({ id: 4, title: 'RxJS', description: 'Reactive', subscribed: true });

    expect(themesApi.unsubscribe).toHaveBeenCalledWith(4);
    expect(themesApi.list).toHaveBeenCalled();
  });
});

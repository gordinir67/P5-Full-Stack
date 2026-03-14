import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { TokenService } from './token.service';
import { environment } from '../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let tokenService: jest.Mocked<TokenService>;

  beforeEach(() => {
    tokenService = {
      token$: undefined as never,
      getToken: jest.fn(),
      setToken: jest.fn(),
      clearToken: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TokenService, useValue: tokenService },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('logs in and stores token', () => {
    let completed = false;

    service.login({ email: 'john@doe.dev', password: 'Secret123!' }).subscribe(() => {
      completed = true;
    });

    const req = httpMock.expectOne(`${environment.baseUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'jwt-token' });

    expect(tokenService.setToken).toHaveBeenCalledWith('jwt-token');
    expect(completed).toBe(true);
  });

  it('registers and stores token', () => {
    service.register({ name: 'John', email: 'john@doe.dev', password: 'Secret123!' }).subscribe();

    const req = httpMock.expectOne(`${environment.baseUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'new-token' });

    expect(tokenService.setToken).toHaveBeenCalledWith('new-token');
  });

  it('logs out by clearing token', () => {
    service.logout();

    expect(tokenService.clearToken).toHaveBeenCalled();
  });

  it('returns true when a token exists', () => {
    tokenService.getToken.mockReturnValue('token');

    expect(service.isLoggedIn()).toBe(true);
  });

  it('returns false when no token exists', () => {
    tokenService.getToken.mockReturnValue(null);

    expect(service.isLoggedIn()).toBe(false);
  });
});

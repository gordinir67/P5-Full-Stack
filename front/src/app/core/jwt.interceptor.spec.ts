import { TestBed } from '@angular/core/testing';
import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpRequest,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { jwtInterceptor } from './jwt.interceptor';
import { TokenService } from '../services/token.service';

class TokenServiceStub {
  token: string | null = null;
  getToken = jest.fn(() => this.token);
  clearToken = jest.fn(() => {
    this.token = null;
  });
}

describe('jwtInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let tokenService: TokenServiceStub;
  let navigate: jest.Mock;

  beforeEach(() => {
    tokenService = new TokenServiceStub();
    navigate = jest.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
        { provide: TokenService, useValue: tokenService },
        { provide: Router, useValue: { navigate } },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('adds the Authorization header when a token exists', () => {
    tokenService.token = 'jwt';

    http.get('/secured').subscribe();

    const req = httpMock.expectOne('/secured');
    expect(req.request.headers.get('Authorization')).toBe('Bearer jwt');
    req.flush({ ok: true });
  });

  it('does not add the Authorization header without token', () => {
    http.get('/public').subscribe();

    const req = httpMock.expectOne('/public');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({ ok: true });
  });

  it('clears token and redirects on 401 response', () => {
    tokenService.token = 'jwt';
    let receivedError: HttpErrorResponse | undefined;

    http.get('/secured').subscribe({
      error: (error) => {
        receivedError = error;
      },
    });

    const req = httpMock.expectOne('/secured');
    req.flush({ message: 'unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    expect(tokenService.clearToken).toHaveBeenCalled();
    expect(navigate).toHaveBeenCalledWith(['/login']);
    expect(receivedError?.status).toBe(401);
  });

  it('passes through non-401 errors without redirecting', () => {
    let receivedError: HttpErrorResponse | undefined;

    http.get('/secured').subscribe({
      error: (error) => {
        receivedError = error;
      },
    });

    const req = httpMock.expectOne('/secured');
    req.flush({ message: 'boom' }, { status: 500, statusText: 'Server Error' });

    expect(tokenService.clearToken).not.toHaveBeenCalled();
    expect(navigate).not.toHaveBeenCalled();
    expect(receivedError?.status).toBe(500);
  });
});

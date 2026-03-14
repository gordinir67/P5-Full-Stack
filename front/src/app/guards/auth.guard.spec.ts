import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { TokenService } from '../services/token.service';

describe('authGuard', () => {
  it('allows navigation when a token exists', () => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TokenService, useValue: { getToken: () => 'token' } },
        { provide: Router, useValue: { parseUrl: jest.fn() } },
      ],
    });

    const result = TestBed.runInInjectionContext(() => authGuard(null as never, null as never));

    expect(result).toBe(true);
  });

  it('redirects to login when no token exists', () => {
    const parseUrl = jest.fn().mockReturnValue('/login-tree');

    TestBed.configureTestingModule({
      providers: [
        { provide: TokenService, useValue: { getToken: () => null } },
        { provide: Router, useValue: { parseUrl } },
      ],
    });

    const result = TestBed.runInInjectionContext(() => authGuard(null as never, null as never));

    expect(parseUrl).toHaveBeenCalledWith('/login');
    expect(result).toBe('/login-tree');
  });
});

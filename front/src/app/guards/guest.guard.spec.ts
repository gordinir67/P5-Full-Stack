import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { guestGuard } from './guest.guard';
import { TokenService } from '../services/token.service';

describe('guestGuard', () => {
  it('redirects authenticated users to articles', () => {
    const parseUrl = jest.fn().mockReturnValue('/articles-tree');

    TestBed.configureTestingModule({
      providers: [
        { provide: TokenService, useValue: { getToken: () => 'token' } },
        { provide: Router, useValue: { parseUrl } },
      ],
    });

    const result = TestBed.runInInjectionContext(() => guestGuard(null as never, null as never));

    expect(parseUrl).toHaveBeenCalledWith('/articles');
    expect(result).toBe('/articles-tree');
  });

  it('allows guests', () => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TokenService, useValue: { getToken: () => null } },
        { provide: Router, useValue: { parseUrl: jest.fn() } },
      ],
    });

    const result = TestBed.runInInjectionContext(() => guestGuard(null as never, null as never));

    expect(result).toBe(true);
  });
});

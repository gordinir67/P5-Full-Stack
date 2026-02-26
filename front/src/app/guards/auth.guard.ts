import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const authGuard: CanActivateFn = () => {
  const tokenSvc = inject(TokenService);
  const router = inject(Router);

  if (tokenSvc.getToken()) {
    return true;
  }

  return router.parseUrl('/login');
};
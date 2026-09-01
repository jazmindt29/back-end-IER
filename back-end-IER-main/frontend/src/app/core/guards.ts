import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () =>
  inject(AuthService).isLoggedIn || inject(Router).createUrlTree(['/login']);

export const adminGuard: CanActivateFn = () =>
  inject(AuthService).isAdmin || inject(Router).createUrlTree(['/login']);

export const investigadorGuard: CanActivateFn = () =>
  inject(AuthService).isInvestigador || inject(Router).createUrlTree(['/login']);

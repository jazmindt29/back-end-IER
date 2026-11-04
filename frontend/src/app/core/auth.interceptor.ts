import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { API_URL } from './api.constants';
import { sesionActual } from './session-token';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const perteneceApi = req.url === API_URL || req.url.startsWith(API_URL + '/');
  const ruta = req.url.split('?')[0];
  const publica = ruta === API_URL + '/auth/login'
    || ruta === API_URL + '/auth/configurar-cuenta'
    || (req.method === 'GET' && ruta.startsWith(API_URL + '/auth/invitaciones/'));
  if (!perteneceApi || publica) return next(req);
  const sesion = sesionActual();
  return next(sesion ? req.clone({ setHeaders: { Authorization: 'Bearer ' + sesion.token } }) : req).pipe(
    catchError((error: unknown) => {
      if (sesion && error instanceof HttpErrorResponse && error.status === 401) {
        // No borrar una sesión nueva si esta petición pertenecía a la anterior.
        if (localStorage.getItem('ier_token') === sesion.token) localStorage.removeItem('ier_token');
      }
      return throwError(() => error);
    }),
  );
};

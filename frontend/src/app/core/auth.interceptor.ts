import { HttpInterceptorFn } from '@angular/common/http';

// Adjunta el JWT guardado a toda petición saliente
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('ier_token');
  return next(
    token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req,
  );
};

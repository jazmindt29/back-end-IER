import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Excluir rutas que no deben llevar token (Login o endpoints de autenticación)
  if (req.url.includes('/auth/login')) {
    return next(req); // Pasa la petición intacta, sin modificar headers
  }

  // 2. Para el resto de peticiones, obtenemos el token
  const token = localStorage.getItem('ier_token');
  
  // 3. Si existe, lo clonamos en la cabecera
  if (token) {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  // 4. Si no hay token, pasa normal (útil para visitantes no logueados viendo proyectos)
  return next(req);
};
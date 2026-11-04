import { HttpErrorResponse } from '@angular/common/http';

export function mensajeHttp(error: unknown, alternativa: string): string {
  if (!(error instanceof HttpErrorResponse)) return alternativa;
  if (error.status === 0) return 'No se pudo conectar con el servidor. Comprueba la conexión e intenta de nuevo.';
  if (error.status === 401) return 'La sesión no es válida o ha expirado. Inicia sesión nuevamente.';
  if (error.status === 403) return 'No tienes permisos para realizar esta operación.';
  if (error.status >= 500) return 'El servidor no pudo completar la operación. Intenta de nuevo más tarde.';
  return typeof error.error?.mensaje === 'string' ? error.error.mensaje : alternativa;
}

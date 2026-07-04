import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from './models';
import { API_URL as API } from './api.constants';

const TOKEN_KEY = 'ier_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${API}/auth/login`, { username, password })
      .pipe(tap((res) => localStorage.setItem(TOKEN_KEY, res.token)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  // Payload del JWT decodificado (claims: sub, rol, userId, investigadorId, exp)
  private get payload(): any | null {
    const t = this.token;
    if (!t) return null;
    try {
      return JSON.parse(atob(t.split('.')[1]));
    } catch {
      return null;
    }
  }

  get isLoggedIn(): boolean {
    const p = this.payload;
    return !!p && p.exp * 1000 > Date.now();
  }

  get username(): string | null {
    return this.payload?.sub ?? null;
  }

  get rol(): string | null {
    return this.payload?.rol ?? null;
  }

  get userId(): number | null {
    return this.payload?.userId ?? null;
  }

  get investigadorId(): number | null {
    return this.payload?.investigadorId ?? null;
  }

  // Cubre tanto "ADMIN" como "ADMINISTRADOR" (según cómo esté nombrado el rol en BD)
  get isAdmin(): boolean {
    return this.isLoggedIn && (this.rol ?? '').toUpperCase().startsWith('ADMIN');
  }

  get isInvestigador(): boolean {
    return this.isLoggedIn && (this.rol ?? '').toUpperCase() === 'INVESTIGADOR';
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Auditoria, Aviso, Componente, Investigador,
  Laboratorio, Proyecto, Reserva, Usuario,
} from './models';

import { API_URL as API } from './api.constants';

export interface Contacto {
  id?: number;
  nombre: string;
  email: string;
  asunto?: string;
  mensaje: string;
}

// Todas las llamadas HTTP al backend en un solo lugar
@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  // --- Avisos ---
  getAvisos(): Observable<Aviso[]> { return this.http.get<Aviso[]>(`${API}/avisos`); }
  crearAviso(a: Partial<Aviso>) { return this.http.post<Aviso>(`${API}/avisos`, a); }
  actualizarAviso(id: number, a: Partial<Aviso>) { return this.http.put<Aviso>(`${API}/avisos/${id}`, a); }
  eliminarAviso(id: number) { return this.http.delete<void>(`${API}/avisos/${id}`); }

  // --- Proyectos ---
  getProyectos(): Observable<Proyecto[]> { return this.http.get<Proyecto[]>(`${API}/proyectos`); }
  getProyectosDestacados(): Observable<Proyecto[]> { return this.http.get<Proyecto[]>(`${API}/proyectos/destacados`); }
  crearProyecto(p: Partial<Proyecto>) { return this.http.post<Proyecto>(`${API}/proyectos`, p); }
  actualizarProyecto(id: number, p: Partial<Proyecto>) { return this.http.put<Proyecto>(`${API}/proyectos/${id}`, p); }
  eliminarProyecto(id: number) { return this.http.delete<void>(`${API}/proyectos/${id}`); }

  // --- Laboratorios ---
  getLaboratorios(): Observable<Laboratorio[]> { return this.http.get<Laboratorio[]>(`${API}/laboratorios`); }
  crearLaboratorio(l: Partial<Laboratorio>) { return this.http.post<Laboratorio>(`${API}/laboratorios`, l); }
  actualizarLaboratorio(id: number, l: Partial<Laboratorio>) { return this.http.put<Laboratorio>(`${API}/laboratorios/${id}`, l); }
  eliminarLaboratorio(id: number) { return this.http.delete<void>(`${API}/laboratorios/${id}`); }

  // --- Componentes / materiales ---
  getComponentes(laboratorioId?: number): Observable<Componente[]> {
    const params = laboratorioId ? new HttpParams().set('laboratorioId', laboratorioId) : undefined;
    return this.http.get<Componente[]>(`${API}/componentes`, { params });
  }
  crearComponente(c: Partial<Componente>) { return this.http.post<Componente>(`${API}/componentes`, c); }
  actualizarComponente(id: number, c: Partial<Componente>) { return this.http.put<Componente>(`${API}/componentes/${id}`, c); }
  eliminarComponente(id: number) { return this.http.delete<void>(`${API}/componentes/${id}`); }

  // --- Investigadores ---
  getInvestigadores(area?: string): Observable<Investigador[]> {
    const params = area ? new HttpParams().set('area', area) : undefined;
    return this.http.get<Investigador[]>(`${API}/investigadores`, { params });
  }
  getMiPerfil(): Observable<Investigador> { return this.http.get<Investigador>(`${API}/investigadores/mi-perfil`); }
  actualizarInvestigador(id: number, i: Partial<Investigador>) { return this.http.put<Investigador>(`${API}/investigadores/${id}`, i); }
  eliminarInvestigador(id: number) { return this.http.delete<void>(`${API}/investigadores/${id}`); }

  // --- Reservas / calendario ---
  getReservas(investigadorId?: number): Observable<Reserva[]> {
    const params = investigadorId ? new HttpParams().set('investigadorId', investigadorId) : undefined;
    return this.http.get<Reserva[]>(`${API}/reservas`, { params });
  }
  crearReserva(r: Partial<Reserva>) { return this.http.post<Reserva>(`${API}/reservas`, r); }
  eliminarReserva(id: number) { return this.http.delete<void>(`${API}/reservas/${id}`); }

  // --- Contacto ---
  enviarContacto(c: Contacto) { return this.http.post<Contacto>(`${API}/contacto`, c); }

  // --- Invitaciones y activación de cuenta ---
  invitarInvestigador(nombre: string, email: string) {
    return this.http.post<{ mensaje: string }>(`${API}/auth/invitaciones`, { nombre, email });
  }
  validarTokenInvitacion(token: string) {
    return this.http.get<{ valido: boolean; email: string }>(`${API}/auth/invitaciones/${token}`);
  }
  configurarCuenta(token: string, password: string) {
    return this.http.post<{ mensaje: string }>(`${API}/auth/configurar-cuenta`, { token, password });
  }

  // --- Administración ---
  getUsuarios(): Observable<Usuario[]> { return this.http.get<Usuario[]>(`${API}/usuarios`); }
  getAuditoria(): Observable<Auditoria[]> { return this.http.get<Auditoria[]>(`${API}/auditoria`); }

  // En ApiService.ts
guardarMiPerfil(perfil: Investigador): Observable<Investigador> {
  // En lugar de pasar /api/investigadores/{id}, llamamos al endpoint de sesión
  return this.http.put<Investigador>(`${API}/investigadores/mi-perfil`, perfil);
}
}

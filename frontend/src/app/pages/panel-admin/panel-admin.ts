import { Component, OnInit, inject, signal } from '@angular/core';
import { Observable, Subscription, finalize, forkJoin, tap } from 'rxjs';
import { DestroyRef } from '@angular/core';
import { mensajeHttp } from '../../core/http-error';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, ProyectoRequest } from '../../core/api.service';
import { InvitacionesAdminComponent } from './invitaciones-admin';
import {
  Auditoria, Aviso, Componente, Investigador, Laboratorio, Proyecto,
} from '../../core/models';

type Pestania = 'invitaciones' | 'avisos' | 'proyectos' | 'laboratorios' | 'componentes' | 'investigadores' | 'auditoria';

@Component({
  selector: 'app-panel-admin',
  standalone: true,
  imports: [FormsModule, DatePipe, InvitacionesAdminComponent],
  templateUrl: './panel-admin.html'
})
export class PanelAdminComponent implements OnInit {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);
  private carga?: Subscription;
  cargando = signal(false);
  guardando = signal(false);

  pestania: Pestania = 'invitaciones';
  mensaje = '';
  error = '';

  avisos: Aviso[] = [];
  proyectos: Proyecto[] = [];
  laboratorios: Laboratorio[] = [];
  componentes: Componente[] = [];
  investigadores: Investigador[] = [];
  auditoria: Auditoria[] = [];

  // Modelos de edición: null = sin formulario abierto; id undefined = creación
  avisoEdit: Partial<Aviso> | null = null;
  proyectoEdit: (Partial<Proyecto> & { investigadorIds?: number[] }) | null = null;
  laboratorioEdit: (Partial<Laboratorio> & { encargadoId?: number | null }) | null = null;
  componenteEdit: (Partial<Componente> & { laboratorioId?: number | null }) | null = null;
  investigadorEdit: Partial<Investigador> | null = null;

  ngOnInit(): void {
    this.destroyRef.onDestroy(() => this.carga?.unsubscribe());
    this.cargarTodo();
  }

  cambiar(p: Pestania): void {
    this.pestania = p;
    this.mensaje = '';
    this.error = '';
    this.cargarTodo();
  }

  private cargarTodo(): void {
    this.carga?.unsubscribe();
    const tareas: Observable<unknown>[] = [];
    if (this.pestania === 'avisos') tareas.push(this.api.getAvisos().pipe(tap(x => this.avisos = x)));
    if (this.pestania === 'proyectos') tareas.push(this.api.getProyectos().pipe(tap(x => this.proyectos = x)));
    if (['laboratorios', 'componentes'].includes(this.pestania)) tareas.push(this.api.getLaboratorios().pipe(tap(x => this.laboratorios = x)));
    if (this.pestania === 'componentes') tareas.push(this.api.getComponentes().pipe(tap(x => this.componentes = x)));
    if (['investigadores', 'proyectos', 'laboratorios'].includes(this.pestania)) tareas.push(this.api.getInvestigadores().pipe(tap(x => this.investigadores = x)));
    if (this.pestania === 'auditoria') tareas.push(this.api.getAuditoria().pipe(tap(x => this.auditoria = x)));
    if (tareas.length === 0) return;
    this.cargando.set(true);
    this.carga = forkJoin(tareas).pipe(finalize(() => this.cargando.set(false))).subscribe({
      error: e => this.error = mensajeHttp(e, 'No se pudieron cargar los datos de esta pestaña'),
    });
  }

  private ok(msj: string): void {
    this.mensaje = msj;
    this.error = '';
    this.cargarTodo();
  }

  private fallo(e: unknown): void {
    this.error = mensajeHttp(e, 'La operación falló');
    this.mensaje = '';
  }

  private mutar<T>(solicitud: Observable<T>, alCompletar: () => void): void {
    if (this.guardando()) return;
    this.guardando.set(true);
    this.error = '';
    this.mensaje = '';
    solicitud.pipe(finalize(() => this.guardando.set(false))).subscribe({
      next: alCompletar,
      error: e => this.fallo(e),
    });
  }

  // --- Avisos ---
  guardarAviso(): void {
    if (this.guardando() || !this.avisoEdit) return;
    const a = this.avisoEdit!;
    const pet = a.id ? this.api.actualizarAviso(a.id, a) : this.api.crearAviso(a);
    this.mutar(pet, () => { if (this.avisoEdit === a) this.avisoEdit = null; this.ok('Aviso guardado'); });
  }
  eliminarAviso(id: number): void {
    if (!this.guardando() && window.confirm('¿Confirmas esta eliminación?')) {
      this.mutar(this.api.eliminarAviso(id), () => this.ok('Aviso eliminado'));
    }
  }

  // --- Proyectos ---
  editarProyecto(p?: Proyecto): void {
    this.proyectoEdit = p
      ? { ...p, investigadorIds: (p.investigadores ?? []).map((i) => i.id) }
      : { titulo: '', esDestacado: false, investigadorIds: [] };
  }
  guardarProyecto(): void {
    if (this.guardando() || !this.proyectoEdit) return;
    const p = this.proyectoEdit!;
    const cuerpo: ProyectoRequest = {
      titulo: p.titulo?.trim() ?? '', descripcion: p.descripcion, estado: p.estado,
      esDestacado: p.esDestacado, imagenUrl: p.imagenUrl,
      investigadorIds: p.investigadorIds ?? [],
    };
    const pet = p.id ? this.api.actualizarProyecto(p.id, cuerpo) : this.api.crearProyecto(cuerpo);
    this.mutar(pet, () => { if (this.proyectoEdit === p) this.proyectoEdit = null; this.ok('Proyecto guardado'); });
  }
  eliminarProyecto(id: number): void {
    if (!this.guardando() && window.confirm('¿Confirmas esta eliminación?')) {
      this.mutar(this.api.eliminarProyecto(id), () => this.ok('Proyecto eliminado'));
    }
  }

  // --- Laboratorios ---
  editarLaboratorio(l?: Laboratorio): void {
    this.laboratorioEdit = l ? { ...l, encargadoId: l.encargado?.id ?? null } : { nombre: '', encargadoId: null };
  }
  guardarLaboratorio(): void {
    if (this.guardando() || !this.laboratorioEdit) return;
    const l = this.laboratorioEdit!;
    const cuerpo: Partial<Laboratorio> = {
      nombre: l.nombre, descripcion: l.descripcion, imagenUrl: l.imagenUrl,
      encargado: l.encargadoId ? ({ id: l.encargadoId } as Investigador) : undefined,
    };
    const pet = l.id ? this.api.actualizarLaboratorio(l.id, cuerpo) : this.api.crearLaboratorio(cuerpo);
    this.mutar(pet, () => { if (this.laboratorioEdit === l) this.laboratorioEdit = null; this.ok('Laboratorio guardado'); });
  }
  eliminarLaboratorio(id: number): void {
    if (!this.guardando() && window.confirm('¿Confirmas esta eliminación?')) {
      this.mutar(this.api.eliminarLaboratorio(id), () => this.ok('Laboratorio eliminado'));
    }
  }

  // --- Componentes ---
  editarComponente(c?: Componente): void {
    this.componenteEdit = c ? { ...c, laboratorioId: c.laboratorio?.id ?? null } : { nombre: '', laboratorioId: null };
  }
  guardarComponente(): void {
    if (this.guardando() || !this.componenteEdit) return;
    const c = this.componenteEdit!;
    const cuerpo: Partial<Componente> = {
      nombre: c.nombre, descripcion: c.descripcion, tipo: c.tipo,
      laboratorio: { id: c.laboratorioId! } as Laboratorio,
    };
    const pet = c.id ? this.api.actualizarComponente(c.id, cuerpo) : this.api.crearComponente(cuerpo);
    this.mutar(pet, () => { if (this.componenteEdit === c) this.componenteEdit = null; this.ok('Componente guardado'); });
  }
  eliminarComponente(id: number): void {
    if (!this.guardando() && window.confirm('¿Confirmas esta eliminación?')) {
      this.mutar(this.api.eliminarComponente(id), () => this.ok('Componente eliminado'));
    }
  }

  // --- Investigadores (edición global de perfiles; el alta es vía invitación) ---
  editarInvestigador(i: Investigador): void {
    this.investigadorEdit = { ...i };
  }
  guardarInvestigador(): void {
    if (this.guardando() || !this.investigadorEdit) return;
    const i = this.investigadorEdit!;
    this.mutar(this.api.actualizarInvestigador(i.id!, i), () => {
      if (this.investigadorEdit === i) this.investigadorEdit = null;
      this.ok('Perfil actualizado');
    });
  }
  eliminarInvestigador(id: number): void {
    if (!this.guardando() && window.confirm('¿Confirmas esta eliminación?')) {
      this.mutar(this.api.eliminarInvestigador(id), () => this.ok('Investigador eliminado'));
    }
  }
}

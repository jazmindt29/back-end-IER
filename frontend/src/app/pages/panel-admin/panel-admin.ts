import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import {
  Auditoria, Aviso, Componente, Investigador, Laboratorio, Proyecto,
} from '../../core/models';

type Pestania = 'invitaciones' | 'avisos' | 'proyectos' | 'laboratorios' | 'componentes' | 'investigadores' | 'auditoria';

@Component({
  selector: 'app-panel-admin',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './panel-admin.html'
})
export class PanelAdminComponent implements OnInit {
  private api = inject(ApiService);

  pestania: Pestania = 'invitaciones';
  mensaje = '';
  error = '';

  avisos: Aviso[] = [];
  proyectos: Proyecto[] = [];
  laboratorios: Laboratorio[] = [];
  componentes: Componente[] = [];
  investigadores: Investigador[] = [];
  auditoria: Auditoria[] = [];

  invitacion = { nombre: '', email: '' };

  // Modelos de edición: null = sin formulario abierto; id undefined = creación
  avisoEdit: Partial<Aviso> | null = null;
  proyectoEdit: (Partial<Proyecto> & { investigadorIds?: number[] }) | null = null;
  laboratorioEdit: (Partial<Laboratorio> & { encargadoId?: number | null }) | null = null;
  componenteEdit: (Partial<Componente> & { laboratorioId?: number | null }) | null = null;
  investigadorEdit: Partial<Investigador> | null = null;

  ngOnInit(): void {
    this.cargarTodo();
  }

  cambiar(p: Pestania): void {
    this.pestania = p;
    this.mensaje = '';
    this.error = '';
  }

  private cargarTodo(): void {
    this.api.getAvisos().subscribe((x) => (this.avisos = x));
    this.api.getProyectos().subscribe((x) => (this.proyectos = x));
    this.api.getLaboratorios().subscribe((x) => (this.laboratorios = x));
    this.api.getComponentes().subscribe((x) => (this.componentes = x));
    this.api.getInvestigadores().subscribe((x) => (this.investigadores = x));
    this.api.getAuditoria().subscribe((x) => (this.auditoria = x));
  }

  private ok(msj: string): void {
    this.mensaje = msj;
    this.error = '';
    this.cargarTodo();
  }

  private fallo(e: any): void {
    this.error = e.error?.mensaje ?? 'La operación falló';
    this.mensaje = '';
  }

  // --- Invitaciones ---
  invitar(): void {
    this.api.invitarInvestigador(this.invitacion.nombre, this.invitacion.email).subscribe({
      next: (r) => {
        this.ok(r.mensaje);
        this.invitacion = { nombre: '', email: '' };
      },
      error: (e) => this.fallo(e),
    });
  }

  // --- Avisos ---
  guardarAviso(): void {
    const a = this.avisoEdit!;
    const pet = a.id ? this.api.actualizarAviso(a.id, a) : this.api.crearAviso(a);
    pet.subscribe({ next: () => { this.avisoEdit = null; this.ok('Aviso guardado'); }, error: (e) => this.fallo(e) });
  }
  eliminarAviso(id: number): void {
    this.api.eliminarAviso(id).subscribe({ next: () => this.ok('Aviso eliminado'), error: (e) => this.fallo(e) });
  }

  // --- Proyectos ---
  editarProyecto(p?: Proyecto): void {
    this.proyectoEdit = p
      ? { ...p, investigadorIds: (p.investigadores ?? []).map((i) => i.id) }
      : { titulo: '', esDestacado: false, investigadorIds: [] };
  }
  guardarProyecto(): void {
    const p = this.proyectoEdit!;
    const cuerpo: Partial<Proyecto> = {
      titulo: p.titulo, descripcion: p.descripcion, estado: p.estado,
      esDestacado: p.esDestacado, imagenUrl: p.imagenUrl,
      investigadores: (p.investigadorIds ?? []).map((id) => ({ id }) as Investigador),
    };
    const pet = p.id ? this.api.actualizarProyecto(p.id, cuerpo) : this.api.crearProyecto(cuerpo);
    pet.subscribe({ next: () => { this.proyectoEdit = null; this.ok('Proyecto guardado'); }, error: (e) => this.fallo(e) });
  }
  eliminarProyecto(id: number): void {
    this.api.eliminarProyecto(id).subscribe({ next: () => this.ok('Proyecto eliminado'), error: (e) => this.fallo(e) });
  }

  // --- Laboratorios ---
  editarLaboratorio(l?: Laboratorio): void {
    this.laboratorioEdit = l ? { ...l, encargadoId: l.encargado?.id ?? null } : { nombre: '', encargadoId: null };
  }
  guardarLaboratorio(): void {
    const l = this.laboratorioEdit!;
    const cuerpo: Partial<Laboratorio> = {
      nombre: l.nombre, descripcion: l.descripcion, imagenUrl: l.imagenUrl,
      encargado: l.encargadoId ? ({ id: l.encargadoId } as Investigador) : undefined,
    };
    const pet = l.id ? this.api.actualizarLaboratorio(l.id, cuerpo) : this.api.crearLaboratorio(cuerpo);
    pet.subscribe({ next: () => { this.laboratorioEdit = null; this.ok('Laboratorio guardado'); }, error: (e) => this.fallo(e) });
  }
  eliminarLaboratorio(id: number): void {
    this.api.eliminarLaboratorio(id).subscribe({ next: () => this.ok('Laboratorio eliminado'), error: (e) => this.fallo(e) });
  }

  // --- Componentes ---
  editarComponente(c?: Componente): void {
    this.componenteEdit = c ? { ...c, laboratorioId: c.laboratorio?.id ?? null } : { nombre: '', laboratorioId: null };
  }
  guardarComponente(): void {
    const c = this.componenteEdit!;
    const cuerpo: Partial<Componente> = {
      nombre: c.nombre, descripcion: c.descripcion, tipo: c.tipo,
      laboratorio: { id: c.laboratorioId! } as Laboratorio,
    };
    const pet = c.id ? this.api.actualizarComponente(c.id, cuerpo) : this.api.crearComponente(cuerpo);
    pet.subscribe({ next: () => { this.componenteEdit = null; this.ok('Componente guardado'); }, error: (e) => this.fallo(e) });
  }
  eliminarComponente(id: number): void {
    this.api.eliminarComponente(id).subscribe({ next: () => this.ok('Componente eliminado'), error: (e) => this.fallo(e) });
  }

  // --- Investigadores (edición global de perfiles; el alta es vía invitación) ---
  editarInvestigador(i: Investigador): void {
    this.investigadorEdit = { ...i };
  }
  guardarInvestigador(): void {
    const i = this.investigadorEdit!;
    this.api.actualizarInvestigador(i.id!, i).subscribe({
      next: () => { this.investigadorEdit = null; this.ok('Perfil actualizado'); },
      error: (e) => this.fallo(e),
    });
  }
  eliminarInvestigador(id: number): void {
    this.api.eliminarInvestigador(id).subscribe({ next: () => this.ok('Investigador eliminado'), error: (e) => this.fallo(e) });
  }
}

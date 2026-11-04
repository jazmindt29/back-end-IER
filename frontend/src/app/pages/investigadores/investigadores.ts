import { Component, OnInit, inject, DestroyRef, signal } from '@angular/core';
import { Subscription, finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { mensajeHttp } from '../../core/http-error';
import { ApiService } from '../../core/api.service';
import { Investigador, Proyecto } from '../../core/models';

@Component({
  selector: 'app-investigadores',
  standalone: true,
  templateUrl: './investigadores.html'
})
export class InvestigadoresComponent implements OnInit {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);
  private peticion?: Subscription;
  cargando = signal(false);
  error = '';

  constructor() {
    this.destroyRef.onDestroy(() => this.peticion?.unsubscribe());
  }

  investigadores: Investigador[] = [];
  proyectos: Proyecto[] = [];
  areaActiva = '';
  readonly areas = ['Agua', 'Energía'];

  ngOnInit(): void {
    this.cargar('');
    // Para mostrar los proyectos vinculados a cada investigador
    this.api.getProyectos().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: p => this.proyectos = p,
      error: e => this.error = mensajeHttp(e, 'No se pudieron cargar los proyectos'),
    });
  }

  cargar(area: string): void {
    this.peticion?.unsubscribe();
    this.areaActiva = area;
    this.error = '';
    this.cargando.set(true);
    this.peticion = this.api.getInvestigadores(area || undefined).pipe(finalize(() => this.cargando.set(false))).subscribe({
      next: i => this.investigadores = i,
      error: e => this.error = mensajeHttp(e, 'No se pudieron cargar los investigadores'),
    });
  }

  proyectosDe(investigadorId: number): Proyecto[] {
    return this.proyectos.filter((p) =>
      (p.investigadores ?? []).some((i) => i.id === investigadorId),
    );
  }
}

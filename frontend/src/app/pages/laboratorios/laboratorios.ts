import { Component, OnInit, inject, DestroyRef, signal } from '@angular/core';
import { Subscription, finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { mensajeHttp } from '../../core/http-error';
import { ApiService } from '../../core/api.service';
import { Componente, Laboratorio } from '../../core/models';

@Component({
  selector: 'app-laboratorios',
  standalone: true,
  templateUrl: './laboratorios.html'
})
export class LaboratoriosComponent implements OnInit {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);
  private peticionMateriales?: Subscription;
  cargando = signal(false);
  cargandoMateriales = signal(false);
  error = '';

  constructor() {
    this.destroyRef.onDestroy(() => this.peticionMateriales?.unsubscribe());
  }

  laboratorios: Laboratorio[] = [];
  seleccionado: Laboratorio | null = null;
  materiales: Componente[] = [];

  ngOnInit(): void {
    this.cargando.set(true);
    this.api.getLaboratorios().pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.cargando.set(false))).subscribe({
      next: l => this.laboratorios = l,
      error: e => this.error = mensajeHttp(e, 'No se pudieron cargar los laboratorios'),
    });
  }

  seleccionar(lab: Laboratorio): void {
    this.peticionMateriales?.unsubscribe();
    this.error = '';
    this.seleccionado = lab;
    this.materiales = [];
    this.cargandoMateriales.set(true);
    this.peticionMateriales = this.api.getComponentes(lab.id).pipe(finalize(() => this.cargandoMateriales.set(false))).subscribe({
      next: c => this.materiales = c,
      error: e => this.error = mensajeHttp(e, 'No se pudieron cargar los materiales'),
    });
  }
}

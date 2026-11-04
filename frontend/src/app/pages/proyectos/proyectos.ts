import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { mensajeHttp } from '../../core/http-error';
import { Proyecto } from '../../core/models';

const normalizar = (texto: string) => texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();

@Component({
  selector: 'app-proyectos',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './proyectos.html',
})
export class ProyectosComponent implements OnInit {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);
  proyectos = signal<Proyecto[]>([]);
  busqueda = signal('');
  cargando = signal(false);
  error = signal('');
  visibles = computed(() => {
    const consulta = normalizar(this.busqueda().trim());
    return this.proyectos().filter(p => normalizar([
      p.titulo, p.descripcion ?? '', p.estado ?? '',
      ...(p.investigadores ?? []).map(i => i.nombre),
    ].join(' ')).includes(consulta));
  });

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    if (this.cargando()) return;
    this.error.set('');
    this.cargando.set(true);
    this.api.getProyectos().pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.cargando.set(false)),
    ).subscribe({
      next: proyectos => this.proyectos.set(proyectos),
      error: error => this.error.set(mensajeHttp(error, 'No se pudieron cargar los proyectos')),
    });
  }
}

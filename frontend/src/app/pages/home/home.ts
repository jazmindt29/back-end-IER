import { Component, OnInit, inject, DestroyRef, signal } from '@angular/core';
import { forkJoin, finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { mensajeHttp } from '../../core/http-error';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { Aviso, Proyecto } from '../../core/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './home.html',
    styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);
  cargando = signal(false);
  error = '';

  avisos: Aviso[] = [];
  destacados: Proyecto[] = [];
  expandidos = new Set<string>();

  ngOnInit(): void {
    this.cargando.set(true);
    forkJoin({ avisos: this.api.getAvisos(), destacados: this.api.getProyectosDestacados() })
      .pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.cargando.set(false)))
      .subscribe({
        next: ({ avisos, destacados }) => { this.avisos = avisos; this.destacados = destacados; },
        error: e => this.error = mensajeHttp(e, 'No se pudo cargar el contenido de inicio'),
      });
  }

  toggle(clave: string): void {
    if (this.expandidos.has(clave)) {
      this.expandidos.delete(clave);
    } else {
      this.expandidos.add(clave);
    }
  }

  expandido(clave: string): boolean {
    return this.expandidos.has(clave);
  }
}

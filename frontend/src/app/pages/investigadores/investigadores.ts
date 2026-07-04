import { Component, OnInit, inject } from '@angular/core';
import { ApiService } from '../../core/api.service';
import { Investigador, Proyecto } from '../../core/models';

@Component({
  selector: 'app-investigadores',
  standalone: true,
  templateUrl: './investigadores.html'
})
export class InvestigadoresComponent implements OnInit {
  private api = inject(ApiService);

  investigadores: Investigador[] = [];
  proyectos: Proyecto[] = [];
  areaActiva = '';
  readonly areas = ['Agua', 'Energía'];

  ngOnInit(): void {
    this.cargar('');
    // Para mostrar los proyectos vinculados a cada investigador
    this.api.getProyectos().subscribe((p) => (this.proyectos = p));
  }

  cargar(area: string): void {
    this.areaActiva = area;
    this.api.getInvestigadores(area || undefined).subscribe((i) => (this.investigadores = i));
  }

  proyectosDe(investigadorId: number): Proyecto[] {
    return this.proyectos.filter((p) =>
      (p.investigadores ?? []).some((i) => i.id === investigadorId),
    );
  }
}
